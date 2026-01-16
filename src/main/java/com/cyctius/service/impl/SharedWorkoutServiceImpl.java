package com.cyctius.service.impl;

import com.cyctius.dto.WorkoutDTO;
import com.cyctius.entity.SharedWorkout;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.handler.exception.ExpiredException;
import com.cyctius.handler.exception.NotFoundException;
import com.cyctius.repository.SharedWorkoutRepository;
import com.cyctius.repository.WorkoutRepository;
import com.cyctius.service.SharedWorkoutService;
import com.cyctius.service.WorkoutTransformer;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SharedWorkoutServiceImpl implements SharedWorkoutService {

    private final SharedWorkoutRepository sharedWorkoutRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutTransformer workoutTransformer;

    @Override
    public String shareWorkout(final String workoutId) {
        if (Objects.isNull(workoutId)) {
            throw new BadRequestException("shared.error.workout-id-cannot-be-null");
        }

        if (!workoutRepository.existsById(workoutId)) {
            throw new BadRequestException("workout.error.workout-not-found");
        }

        val sharedWorkout = SharedWorkout.builder()
                .workoutId(workoutId)
                .lifeTimeS(300) // 5 minutes lifetime for the shared workout
                .build();

        return sharedWorkoutRepository.save(sharedWorkout).getId();
    }

    @Override
    public WorkoutDTO getSharedWorkout(final String sharedId) {
        if (Objects.isNull(sharedId)) {
            throw new BadRequestException("shared.error.shared-id-cannot-be-null");
        }

       SharedWorkout sharedWorkout = sharedWorkoutRepository.findById(sharedId).orElseThrow(() -> new NotFoundException("shared.error.shared-workout-not-found"));

        if (sharedWorkout.isExpired()) {
            throw new ExpiredException("shared.error.shared-workout-expired");
        }

        return workoutRepository.findById(sharedWorkout.getWorkoutId())
                .map(workoutTransformer::transformToDTO)
                .orElseThrow(() -> new NotFoundException("workout.error.workout-not-found"));
    }

    @Override
    @Transactional
    public void cleanExpiredSharedWorkouts() {
        val now = LocalDateTime.now(ZoneOffset.UTC).minusMinutes(5);
        sharedWorkoutRepository.deleteByCreatedAtBefore(now);
    }
}
