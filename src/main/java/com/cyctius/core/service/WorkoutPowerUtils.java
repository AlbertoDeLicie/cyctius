package com.cyctius.core.service;

import java.util.Map;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.enums.PowerZone;

public interface WorkoutPowerUtils {
    Map<PowerZone, Integer> calculateDistribution(WorkoutModel model);
    Integer calculateAverageIntensity(WorkoutModel model);
    Integer calculateNormalizedIntensity(WorkoutModel model);
    Double calculateIF(WorkoutModel model);
    Double calculateDifficulty(WorkoutModel model);
    PowerZone powerZoneByIntensity(Integer intensity);
}
