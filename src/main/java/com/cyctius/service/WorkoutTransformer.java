package com.cyctius.service;

import com.cyctius.dto.WorkoutDTO;
import com.cyctius.entity.Workout;

public interface WorkoutTransformer {
    Workout transformToEntity(WorkoutDTO workoutDTO);
    WorkoutDTO transformToDTO(Workout workout);
}
