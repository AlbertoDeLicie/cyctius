package com.cyctius.core.service.impl;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.WorkoutTimeInZone;
import com.cyctius.core.model.intervals.Interval;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.core.service.WorkoutPowerUtils;
import com.cyctius.core.enums.PowerZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.val;

import org.springframework.stereotype.Service;

@Service
public class WorkoutPowerUtilsImpl implements WorkoutPowerUtils {

    @Override
    public Map<PowerZone, Integer> calculateDistribution(final WorkoutModel model) {
        if (Objects.isNull(model) || !model.isValid()) {
            return Collections.emptyMap();
        }

        Map<PowerZone, Integer> distribution = new HashMap<>(
                Map.of(
                        PowerZone.Z1_ACTIVE_RECOVERY, 0,
                        PowerZone.Z2_ENDURANCE, 0,
                        PowerZone.Z3_TEMPO, 0,
                        PowerZone.Z4_THRESHOLD, 0,
                        PowerZone.Z5_VO2MAX, 0,
                        PowerZone.Z6_ANEROBIC_CAPACITY, 0,
                        PowerZone.Z7_NEUROMUSCULAR_POWER, 0));

        for (Interval interval : model.getIntervals()) {
            val timeInZone = getWorkoutTimeInZone(interval);

            if (!timeInZone.isEmpty()) {
                timeInZone.forEach(time -> {
                    distribution.put(time.getPowerZone(),
                            distribution.get(time.getPowerZone()) + time.getTimeInZone());
                });
            }
        }

        return distribution;

    }

    @Override
    public Integer calculateAverageIntensity(final WorkoutModel model) {
        if (Objects.isNull(model) || !model.isValid()) {
            return 0;
        }

        val averageIntensity = model.getIntervals().stream()
            .map(Interval::averageIntensity)
            .reduce(0, (t, u) -> Integer.sum(t, u)) / model.getIntervals().size();

        return averageIntensity;
    }

    @Override
    public Integer calculateNormalizedIntensity(final WorkoutModel model) {
        if (Objects.isNull(model) || !model.isValid()) {
            return 0;
        }

        List<Integer> intensitySeries = expandIntervalsToTimeSeries(model.getIntervals());

        if (intensitySeries.size() < 30) {
            return 0;
        }

        List<Double> smoothedIntensities = applyRollingAverage(intensitySeries, 30);

        double sumOfFourthPowers = 0.0;
        for (Double smoothed : smoothedIntensities) {
            sumOfFourthPowers += Math.pow(smoothed, 4.0);
        }

        double averageOfFourthPowers = sumOfFourthPowers / smoothedIntensities.size();
        double normalizedPower = Math.pow(averageOfFourthPowers, 1.0 / 4.0);

        return (int) Math.round(normalizedPower);
    }

    @Override
    public Double calculateIF(final WorkoutModel model) {
        if (Objects.isNull(model) || !model.isValid()) {
            return 0.0;
        }
        
        Double normalizedIntensity = (double) calculateNormalizedIntensity(model);

        if (normalizedIntensity == 0.0) {
            return 0.0;
        }

        return normalizedIntensity / 100.0;
    }

    @Override
    public Double calculateDifficulty(final WorkoutModel model) {
        if (Objects.isNull(model) || !model.isValid()) {
            return 0.0;
        }

        return Math.max(calculateIF(model) * 10.0, 10.0);
    }

    @Override
    public PowerZone powerZoneByIntensity(final Integer intensity) {
        if (intensity < 55) {
            return PowerZone.Z1_ACTIVE_RECOVERY;
        } else if (intensity < 76) {
            return PowerZone.Z2_ENDURANCE;
        } else if (intensity < 91) {
            return PowerZone.Z3_TEMPO;
        } else if (intensity < 106) {
            return PowerZone.Z4_THRESHOLD;
        } else if (intensity < 121) {
            return PowerZone.Z5_VO2MAX;
        } else if (intensity < 151) {
            return PowerZone.Z6_ANEROBIC_CAPACITY;
        } else {
            return PowerZone.Z7_NEUROMUSCULAR_POWER;
        }
    }

    private List<Integer> expandIntervalsToTimeSeries(final List<Interval> intervals) {
        List<Integer> intensitySeries = new ArrayList<>();

        for (Interval interval : intervals) {
            if (interval instanceof SingleInterval single) {
                intensitySeries.addAll(expandSingleInterval(single));
            } else if (interval instanceof RampInterval ramp) {
                intensitySeries.addAll(expandRampInterval(ramp));
            } else if (interval instanceof RepeatInterval repeat) {
                intensitySeries.addAll(expandRepeatInterval(repeat));
            }
        }

        return intensitySeries;
    }

    private List<Double> applyRollingAverage(final List<Integer> values, final int windowSize) {
        List<Double> smoothed = new ArrayList<>();

        if (values.size() < windowSize) {
            return smoothed;
        }

        double currentWindowSum = 0;
        for (int i = 0; i < windowSize; i++) {
            currentWindowSum += values.get(i);
        }
        smoothed.add(currentWindowSum / windowSize);

        for (int i = windowSize; i < values.size(); i++) {
            currentWindowSum += values.get(i) - values.get(i - windowSize);
            smoothed.add(currentWindowSum / windowSize);
        }

        return smoothed;
    }

    private List<Integer> expandSingleInterval(final SingleInterval interval) {
        List<Integer> intensities = new ArrayList<>();
        for (int i = 0; i < interval.getDuration(); i++) {
            intensities.add(interval.getTargetIntensity());
        }
        return intensities;
    }

    private List<Integer> expandRampInterval(final RampInterval interval) {
        List<Integer> intensities = new ArrayList<>();
        for (int i = 0; i < interval.getDuration(); i++) {
            intensities.add(interval.getTargetIntensityFrom()
                    + (interval.getTargetIntensityTo() - interval.getTargetIntensityFrom()) * i
                            / interval.getDuration());
        }
        return intensities;
    }

    private List<Integer> expandRepeatInterval(final RepeatInterval interval) {
        List<Integer> intensities = new ArrayList<>();
        for (int i = 0; i < interval.getRepeats(); i++) {
            intensities.addAll(expandSingleInterval(interval.getWork()));
            intensities.addAll(expandSingleInterval(interval.getRest()));
        }
        return intensities;
    }

    private List<WorkoutTimeInZone> getWorkoutTimeInZone(final Interval interval) {
        switch (interval.getType()) {
            case SINGLE:
                return List.of(getWorkoutTimeInZone((SingleInterval) interval));
            case RAMP:
                return List.of(getWorkoutTimeInZone((RampInterval) interval));
            case REPEAT:
                return getWorkoutTimeInZone((RepeatInterval) interval);
            default:
                return Collections.emptyList();
        }
    }

    private List<WorkoutTimeInZone> getWorkoutTimeInZone(final RepeatInterval interval) {
        return List.of(
            getWorkoutTimeInZone(interval.getWork()).multiplyTime(interval.getRepeats()),
            getWorkoutTimeInZone(interval.getRest()).multiplyTime(interval.getRepeats())
        );
    }

    private WorkoutTimeInZone getWorkoutTimeInZone(final SingleInterval interval) {
        return new WorkoutTimeInZone(
                interval.getDuration(),
                powerZoneByIntensity(interval.getTargetIntensity()));
    }

    private WorkoutTimeInZone getWorkoutTimeInZone(final RampInterval interval) {
        return new WorkoutTimeInZone(
                interval.getDuration(),
                powerZoneByIntensity(interval.getTargetIntensityFrom()));
    }
}
