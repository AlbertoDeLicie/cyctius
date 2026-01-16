package com.cyctius.service.impl;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.service.WorkoutMetadataCalculator;
import com.cyctius.dto.PlannedWorkoutDTO;
import com.cyctius.dto.WorkoutDTO;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.handler.exception.NotFoundException;
import com.cyctius.repository.PlannedWorkoutRepository;
import com.cyctius.service.InternalUserService;
import com.cyctius.service.PlannedWorkoutService;
import com.cyctius.service.PlannedWorkoutTransformer;
import com.cyctius.service.UserValidator;
import com.cyctius.service.WorkoutTransformer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlannedWorkoutServiceImpl implements PlannedWorkoutService {
    private final PlannedWorkoutRepository plannedWorkoutRepository;
    private final PlannedWorkoutTransformer plannedWorkoutTransformer;
    private final InternalUserService internalUserService;
    private final UserValidator userValidator;
    private final WorkoutMetadataCalculator workoutMetadataCalculator;
    private final WorkoutTransformer workoutTransformer;

    @Override
    public PlannedWorkoutDTO getPlannedWorkoutById(final String id) {
        if (Objects.isNull(id)) {
            throw new NotFoundException("planned-workout.error.id-cannot-be-null");
        }

        val plannedWorkout = plannedWorkoutRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("planned-workout.error.not-found"));

        userValidator.validateAuthor(plannedWorkout, "userId");

        return plannedWorkoutTransformer.transformToDTO(plannedWorkout);
    }

    @Override
    public Page<PlannedWorkoutDTO> getPlannedWorkoutsByTimeRange(
        final LocalDateTime start, 
        final LocalDateTime end, 
        final Pageable pageable
    ) {
        val userId = internalUserService.getCurrentUser().getUserId();
        return plannedWorkoutRepository.findAllByUserIdAndPlannedDateBetween(userId, start, end, pageable)
                .map(plannedWorkoutTransformer::transformToDTO);
    }

    @Override
    public PlannedWorkoutDTO insertPlannedWorkout(final PlannedWorkoutDTO plannedWorkoutDTO) {
        if (Objects.isNull(plannedWorkoutDTO)) {
            throw new BadRequestException("planned-workout.error.cannot-be-null");
        }

        val currentUser = internalUserService.getCurrentUser();
        plannedWorkoutDTO.setUserId(currentUser.getUserId());

        if (plannedWorkoutDTO.getWorkout() != null) {
            val workoutDTO = plannedWorkoutDTO.getWorkout();
            workoutDTO.setAuthorId(currentUser.getUserId());
            calculateAndSetMetadata(workoutDTO);
        }

        val entity = plannedWorkoutTransformer.transformToEntity(plannedWorkoutDTO);
        return plannedWorkoutTransformer.transformToDTO(plannedWorkoutRepository.save(entity));
    }

    @Override
    public PlannedWorkoutDTO updatePlannedWorkout(final String id, final PlannedWorkoutDTO plannedWorkoutDTO) {
        if (Objects.isNull(id) || Objects.isNull(plannedWorkoutDTO)) {
            throw new BadRequestException("planned-workout.error.id-or-dto-cannot-be-null");
        }

        val existing = plannedWorkoutRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("planned-workout.error.not-found"));

        userValidator.validateAuthor(existing, "userId");

        existing.setPlannedDate(plannedWorkoutDTO.getPlannedDate());

        if (plannedWorkoutDTO.getWorkout() != null) {
            val workoutDTO = plannedWorkoutDTO.getWorkout();
            workoutDTO.setAuthorId(internalUserService.getCurrentUser().getUserId());
            calculateAndSetMetadata(workoutDTO);
            existing.setWorkout(workoutTransformer.transformToEntity(workoutDTO));
        }

        return plannedWorkoutTransformer.transformToDTO(plannedWorkoutRepository.save(existing));
    }

    @Override
    public void deletePlannedWorkout(final String id) {
        if (Objects.isNull(id)) {
            throw new BadRequestException("planned-workout.error.id-cannot-be-null");
        }

        val plannedWorkout = plannedWorkoutRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("planned-workout.error.not-found"));

        userValidator.validateAuthor(plannedWorkout, "userId");

        plannedWorkoutRepository.delete(plannedWorkout);
    }

    private void calculateAndSetMetadata(final WorkoutDTO workoutDTO) {
        if (workoutDTO.getIntervals() == null || workoutDTO.getIntervals().isEmpty()) {
            throw new BadRequestException("workout.error.intervals-cannot-be-null-or-empty");
        }

        val workoutModel = WorkoutModel.builder()
                .id(workoutDTO.getId())
                .name(workoutDTO.getName())
                .description(workoutDTO.getDescription())
                .intervals(workoutDTO.getIntervals())
                .build();

        val metadata = workoutMetadataCalculator.calculateMetadata(workoutModel, true);
        workoutDTO.setMetadata(metadata);
    }
}
