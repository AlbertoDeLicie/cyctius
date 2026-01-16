package com.cyctius.core.service.impl;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.intervals.Interval;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.core.service.TssCalculationService;

import org.springframework.stereotype.Service;
import lombok.val;

/**
 * Implementation of TSS calculation service.
 * TSS formula: TSS = Σ(duration_seconds × intensity²) / 3600 * 100
 */
@Service
public class TssCalculationServiceImpl implements TssCalculationService {

    private static final int SECONDS_PER_HOUR = 3600;
    private static final double ROUNDING_FACTOR_TSS = 100.0;

    @Override
    public Integer calculateTssForSession(final WorkoutModel workout) {

        if (workout == null || workout.getIntervals() == null || workout.getIntervals().isEmpty()) {
            return 0;
        }

        return workout.getIntervals().stream()
            .mapToInt(this::calculateTssForInterval)
            .sum();
    }

    @Override
    public Integer calculateTssForInterval(final Interval interval) {

        if (interval == null) {
            return 0;
        }

        return switch (interval.getType()) {
            case SINGLE -> calculateTssForSingleInterval((SingleInterval) interval);
            case REPEAT -> calculateTssForRepeatInterval((RepeatInterval) interval);
            case RAMP -> calculateTssForRampInterval((RampInterval) interval);
        };
    }

    private Integer calculateTssForSingleInterval(final SingleInterval interval) {
        return calculateTss(interval.getTargetIntensity(), interval.getDuration());
    }

    private Integer calculateTssForRepeatInterval(final RepeatInterval interval) {
        val workTss = calculateTssForSingleInterval(interval.getWork());
        val restTss = calculateTssForSingleInterval(interval.getRest());
        return (workTss + restTss) * interval.getRepeats();
    }

    private Integer calculateTssForRampInterval(final RampInterval interval) {
        val averageIntensity = (interval.getTargetIntensityFrom() + interval.getTargetIntensityTo()) / 2;
        return calculateTss(averageIntensity, interval.getDuration());
    }

    private Integer calculateTss(Integer intensity, Integer duration) {
        return (int) Math.round((double) (duration * intensity * intensity) / SECONDS_PER_HOUR / ROUNDING_FACTOR_TSS);
    }
}
