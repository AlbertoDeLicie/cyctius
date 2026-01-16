package com.cyctius.core.service;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.intervals.Interval;

/**
 * Service for calculating Training Stress Score (TSS) for workouts and intervals.
 * TSS is calculated using percentage-based intensities (0.0-1.0 scale).
 */
public interface TssCalculationService {
    
    /**
     * Calculate TSS for a complete workout session.
     *
     * @param workout the workout model containing intervals
     * @return TSS calculation result with TSS, normalized power, and intensity factor
     */
    Integer calculateTssForSession(WorkoutModel workout);
    
    /**
     * Calculate TSS for a single interval.
     *
     * @param interval the interval to calculate TSS for
     * @return the TSS value for this interval
     */
    Integer calculateTssForInterval(Interval interval);

}

