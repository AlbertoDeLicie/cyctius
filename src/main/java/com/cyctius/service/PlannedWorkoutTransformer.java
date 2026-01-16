package com.cyctius.service;

import com.cyctius.dto.PlannedWorkoutDTO;
import com.cyctius.entity.PlannedWorkout;

public interface PlannedWorkoutTransformer {
    PlannedWorkout transformToEntity(PlannedWorkoutDTO plannedWorkoutDTO);
    PlannedWorkoutDTO transformToDTO(PlannedWorkout plannedWorkout);
}
