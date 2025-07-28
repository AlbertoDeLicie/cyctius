package com.cyctius.service;

public interface SoftDeletedWorkoutCleaner {
    /**
     * Cleans up soft-deleted workouts.
     * This method should be called periodically to remove soft-deleted workouts that are no longer needed.
     */
    void cleanSoftDeletedWorkouts();
}
