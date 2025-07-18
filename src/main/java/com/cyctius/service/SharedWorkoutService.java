package com.cyctius.service;

import com.cyctius.dto.SharedWorkoutDTO;
import com.cyctius.dto.WorkoutDTO;

public interface SharedWorkoutService {
    /**
     * Share a workout by its ID.
     *
     * @param workoutId the ID of the workout to share
     * @return the ID of the shared workout
     */
    String shareWorkout(String workoutId);

    /**
     * Get a shared workout by its ID.
     *
     * @param sharedId the ID of the shared workout
     * @return the shared workout details
     */
    SharedWorkoutDTO getSharedWorkout(String sharedId);

    /**
     * Clean up expired shared workouts.
     */
    void cleanExpiredSharedWorkouts();
}
