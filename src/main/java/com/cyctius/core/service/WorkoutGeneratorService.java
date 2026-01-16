package com.cyctius.core.service;

import com.cyctius.core.enums.WorkoutType;
import com.cyctius.core.model.WorkoutModel;

public interface WorkoutGeneratorService {
    WorkoutModel generateWorkout(
        WorkoutType workoutType,
        Integer totalTSS,
        Double difficulty, 
        Integer totalDurationSeconds, 
        Integer mainSetDurationSeconds
    );
}
