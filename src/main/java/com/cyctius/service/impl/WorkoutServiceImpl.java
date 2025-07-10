package com.cyctius.service.impl;

import com.cyctius.dto.WorkoutDTO;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.handler.exception.NotFoundException;
import com.cyctius.repository.WorkoutRepository;
import com.cyctius.service.WorkoutService;
import com.cyctius.service.WorkoutTransformer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutTransformer workoutTransformer;
    private final WorkoutRepository workoutRepository;

    @Override
    public WorkoutDTO insertWorkout(final WorkoutDTO workoutDTO) {
        if (Objects.isNull(workoutDTO)) {
            throw new BadRequestException("workout.error.workout-cannot-be-null");
        }

        if (Objects.isNull(workoutDTO.getId())) {
            val workout = workoutTransformer.transformToEntity(workoutDTO);
            return workoutTransformer.transformToDTO(workoutRepository.save(workout));
        } else {
            return updateWorkout(workoutDTO);
        }
    }

    @Override
    public List<WorkoutDTO> insertWorkouts(final List<WorkoutDTO> workoutDTOs) {
        if (Objects.isNull(workoutDTOs) || workoutDTOs.isEmpty()) {
            throw new BadRequestException("workout.error.workouts-cannot-be-null-or-empty");
        }

        val result = workoutRepository.saveAll(
                workoutDTOs.stream()
                        .map(workoutTransformer::transformToEntity)
                        .toList()
        );

        return StreamSupport.stream(result.spliterator(), false)
                .map(workoutTransformer::transformToDTO)
                .toList();
    }

    @Override
    public WorkoutDTO updateWorkout(final WorkoutDTO workoutDTO) {
        if (Objects.isNull(workoutDTO)) {
            throw new BadRequestException("workout.error.workout-cannot-be-null");
        }

        if (Objects.isNull(workoutDTO.getId())) {
            throw new BadRequestException("workout.error.workout-id-cannot-be-null");
        }

        val existedWorkout = workoutRepository.findById(workoutDTO.getId()).orElseThrow(
                () -> new NotFoundException("workout.error.workout-not-found")
        );

        if (!Objects.equals(workoutDTO.getAuthorId(), existedWorkout.getAuthorId())) {
            return workoutDTO;
        }

        if (existedWorkout.getIsSoftDeleted()) {
            throw new BadRequestException("workout.error.workout-not-found");
        }

        if (workoutDTO.getUpdatedAt().isBefore(existedWorkout.getUpdatedAt())) {
            return workoutTransformer.transformToDTO(existedWorkout);
        }

        val currentSystemTime = LocalDateTime.now();

        if (workoutDTO.getUpdatedAt().isAfter(currentSystemTime)) {
            throw new BadRequestException("workout.error.updated-at-cannot-be-in-future");
        }

        existedWorkout.setName(workoutDTO.getName());
        existedWorkout.setDescription(workoutDTO.getDescription());
        existedWorkout.setIntervalsJson(workoutDTO.getIntervalsJson());
        existedWorkout.setUpdatedAt(workoutDTO.getUpdatedAt());

        return workoutTransformer.transformToDTO(workoutRepository.save(existedWorkout));
    }

    @Override
    public WorkoutDTO getWorkoutById(final String id) {
        if (Objects.isNull(id)) {
            throw new NotFoundException("workout.error.workout-id-cannot-be-null");
        }

        val workout = workoutRepository.findById(id)
                .map(workoutTransformer::transformToDTO)
                .orElseThrow(() -> new NotFoundException("workout.error.workout-not-found"));

        if (workout.getIsSoftDeleted()) {
            throw new BadRequestException("workout.error.workout-not-found");
        } else {
            return workout;
        }
    }

    @Override
    public void softDeleteWorkout(final String id) {
        if (Objects.isNull(id)) {
            throw new BadRequestException("workout.error.workout-id-cannot-be-null");
        }

        val workout = workoutRepository.findById(id).orElseThrow(
                () -> new NotFoundException("workout.error.workout-not-found")
        );

        workout.setIsSoftDeleted(true);

        workoutRepository.save(workout);
    }

    @Override
    public List<WorkoutDTO> getAllWorkouts(final String authorId) {
        if (Objects.isNull(authorId)) {
            throw new BadRequestException("workout.error.author-id-cannot-be-null");
        }

        return workoutRepository.findAllByAuthorId(authorId).stream()
                .map(workoutTransformer::transformToDTO)
                .filter(workout -> !workout.getIsSoftDeleted())
                .toList();
    }

    @Override
    public List<WorkoutDTO> getWorkoutsPage(final Integer page, final Integer size, final String authorId) {
        return workoutRepository.findAllByAuthorId(authorId, PageRequest.of(page, size)).stream()
                .map(workoutTransformer::transformToDTO)
                .filter(workout -> !workout.getIsSoftDeleted())
                .toList();
    }
}
