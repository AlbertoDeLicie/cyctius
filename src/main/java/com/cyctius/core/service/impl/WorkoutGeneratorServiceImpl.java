package com.cyctius.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyctius.core.enums.WorkoutType;
import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.service.WorkoutGeneratorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkoutGeneratorServiceImpl implements WorkoutGeneratorService {

    @Override
    public WorkoutModel generateWorkout(
        final WorkoutType workoutType, 
        final Integer totalTSS,
        final Double difficulty, 
        final Integer totalDurationSeconds, 
        final Integer mainSetDurationSeconds
    ) {
        return null;
    }

    @SuppressWarnings("unused")
    private WorkoutModel generateRecoveryWorkout(
        final Double difficulty, 
        final Integer totalDurationSeconds, 
        final Integer mainSetDurationSeconds
    ) {
        return null;
    }

    @SuppressWarnings("unused")
    private WorkoutModel generateEnduranceWorkout(
        final Double difficulty, 
        final Integer totalDurationSeconds, 
        final Integer mainSetDurationSeconds
    ) {
        return null;
    }

    @SuppressWarnings("unused")
    private WorkoutModel generateTempoWorkout(
        final Double difficulty,
        final Integer totalDurationSeconds,
        final Integer mainSetDurationSeconds
    ) {
        return null;
    }

    @SuppressWarnings("unused")
    private WorkoutModel generateThresholdWorkout(
        final Double difficulty,
        final Integer totalDurationSeconds,
        final Integer mainSetDurationSeconds
    ) {
        return null;
    }
    
    @SuppressWarnings("unused")
    private WorkoutModel generateVO2maxWorkout(
        final Double difficulty,
        final Integer totalDurationSeconds,
        final Integer mainSetDurationSeconds
    ) {
        return null;
    }
    
    @SuppressWarnings("unused")
    private WorkoutModel generateNeuromuscularWorkout(
        final Double difficulty,
        final Integer totalDurationSeconds,
        final Integer mainSetDurationSeconds
    ) {
        return null;
    }
}
