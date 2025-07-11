package com.cyctius.service;

import com.cyctius.dto.SharedWorkoutDTO;
import com.cyctius.entity.Workout;

public interface SharedWorkoutTransformer {
    SharedWorkoutDTO transformFromEntity(Workout workout);
}
