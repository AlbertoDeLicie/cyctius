package com.cyctius.service;

public interface SharedWorkoutExpiredCleaner {
    /**
     * Cleans up expired shared workouts.
     * This method should be called periodically to remove shared workouts that have expired.
     */
    void cleanExpiredSharedWorkouts();
}
