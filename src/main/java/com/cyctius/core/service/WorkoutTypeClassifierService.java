package com.cyctius.core.service;

import com.cyctius.core.enums.WorkoutType;
import com.cyctius.core.model.WorkoutModel;

/**
 * Service for classifying workouts by training type based on interval intensities.
 */
public interface WorkoutTypeClassifierService {
    
    /**
     * Classify a workout by analyzing its intervals and determining the dominant training type.
     *
     * @param workout the workout model to classify
     * @return the training type classification
     */
    WorkoutType classifyWorkout(WorkoutModel workout);
}

