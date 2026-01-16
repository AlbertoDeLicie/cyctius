package com.cyctius.core.service.impl;

import com.cyctius.core.enums.WorkoutType;
import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.enums.PowerZone;
import com.cyctius.core.service.WorkoutTypeClassifierService;
import com.cyctius.core.service.WorkoutPowerUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of training type classification service.
 * Classifies workouts based on distribution of time in intensity zones.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkoutTypeClassifierServiceImpl implements WorkoutTypeClassifierService {

    private final WorkoutPowerUtils workoutPowerUtils;

    // Thresholds for each zone (percentage of total time)
    private static final double Z7_THRESHOLD = 0.05; // 5% for Neuromuscular
    private static final double Z6_THRESHOLD = 0.08; // 8% for Anaerobic
    private static final double Z5_THRESHOLD = 0.10; // 10% for VO2 Max
    private static final double Z4_THRESHOLD = 0.20; // 15% for Threshold
    private static final double Z3_THRESHOLD = 0.40; // 20% for Tempo
    private static final double Z2_THRESHOLD = 0.60; // 25% for Endurance

    @Override
    public WorkoutType classifyWorkout(final WorkoutModel workout) {
        if (workout == null || workout.getIntervals() == null || workout.getIntervals().isEmpty()) {
            return WorkoutType.RECOVERY;
        }

        Map<PowerZone, Integer> distribution = workoutPowerUtils.calculateDistribution(workout);
        if (distribution.isEmpty()) {
            return WorkoutType.RECOVERY;
        }

        int totalDuration = workout.getTotalDurationSeconds();
        if (totalDuration == 0) {
            return WorkoutType.RECOVERY;
        }

        // We check zones from top to bottom using zone-specific thresholds
        double cumulativeTime = 0;

        // Neuromuscular (Z7)
        cumulativeTime += distribution.getOrDefault(PowerZone.Z7_NEUROMUSCULAR_POWER, 0);
        if (cumulativeTime / totalDuration >= Z7_THRESHOLD) {
            return WorkoutType.NEUROMUSCULAR;
        }

        // Anaerobic (Z6)
        cumulativeTime += distribution.getOrDefault(PowerZone.Z6_ANEROBIC_CAPACITY, 0);
        if (cumulativeTime / totalDuration >= Z6_THRESHOLD) {
            return WorkoutType.ANAEROBIC;
        }

        // VO2 Max (Z5)
        cumulativeTime += distribution.getOrDefault(PowerZone.Z5_VO2MAX, 0);
        if (cumulativeTime / totalDuration >= Z5_THRESHOLD) {
            return WorkoutType.VO2MAX;
        }

        // Threshold (Z4)
        cumulativeTime += distribution.getOrDefault(PowerZone.Z4_THRESHOLD, 0);
        if (cumulativeTime / totalDuration >= Z4_THRESHOLD) {
            return WorkoutType.THRESHOLD;
        }

        // Tempo (Z3)
        cumulativeTime += distribution.getOrDefault(PowerZone.Z3_TEMPO, 0);
        if (cumulativeTime / totalDuration >= Z3_THRESHOLD) {
            return WorkoutType.TEMPO;
        }

        // Endurance (Z2)
        cumulativeTime += distribution.getOrDefault(PowerZone.Z2_ENDURANCE, 0);
        if (cumulativeTime / totalDuration >= Z2_THRESHOLD) {
            return WorkoutType.ENDURANCE;
        }

        // Default to recovery
        return WorkoutType.RECOVERY;
    }
}

