package com.cyctius.core.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.service.WorkoutTypeClassifierService;
import com.cyctius.core.service.TssCalculationService;
import com.cyctius.core.service.WorkoutMetadataCalculator;
import com.cyctius.core.service.WorkoutPowerUtils;
import com.cyctius.dto.WorkoutMetadataDTO;
import com.cyctius.handler.exception.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkoutMetadataCalculatorImpl implements WorkoutMetadataCalculator {

    private final WorkoutPowerUtils workoutPowerUtils;
    private final TssCalculationService tssCalculationService;
    private final WorkoutTypeClassifierService trainingTypeClassifierService;

    @Override
    public WorkoutMetadataDTO calculateMetadata(final WorkoutModel workout, final Boolean forceRecalculate) {
        if (Objects.isNull(workout)) {
            throw new BadRequestException("workout.error.workout-not-found");
        }

        if (workout.getMetadata() != null && Objects.nonNull(forceRecalculate) && !forceRecalculate) {
            return workout.getMetadata();
        }

        val metadata = WorkoutMetadataDTO.builder()
                .workoutId(workout.getId())
                .estimatedTss(tssCalculationService.calculateTssForSession(workout))
                .trainingType(trainingTypeClassifierService.classifyWorkout(workout))
                .difficulty(workoutPowerUtils.calculateDifficulty(workout))
                .intensityFactor(workoutPowerUtils.calculateIF(workout))
                .averageIntensity(workoutPowerUtils.calculateAverageIntensity(workout))
                .durationSeconds(workout.getTotalDurationSeconds())
                .build();

        return metadata;
    }
}
