package com.cyctius.service.impl;

import com.cyctius.dto.PlannedWorkoutDTO;
import com.cyctius.entity.PlannedWorkout;
import com.cyctius.service.PlannedWorkoutTransformer;
import com.cyctius.service.WorkoutTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlannedWorkoutTransformerImpl implements PlannedWorkoutTransformer {
    private final WorkoutTransformer workoutTransformer;

    @Override
    public PlannedWorkout transformToEntity(PlannedWorkoutDTO plannedWorkoutDTO) {
        if (plannedWorkoutDTO == null) {
            return null;
        }

        return PlannedWorkout.builder()
                .id(plannedWorkoutDTO.getId())
                .userId(plannedWorkoutDTO.getUserId())
                .workout(workoutTransformer.transformToEntity(plannedWorkoutDTO.getWorkout()))
                .plannedDate(plannedWorkoutDTO.getPlannedDate())
                .createdAt(plannedWorkoutDTO.getCreatedAt())
                .updatedAt(plannedWorkoutDTO.getUpdatedAt())
                .build();
    }

    @Override
    public PlannedWorkoutDTO transformToDTO(PlannedWorkout plannedWorkout) {
        if (plannedWorkout == null) {
            return null;
        }

        return PlannedWorkoutDTO.builder()
                .id(plannedWorkout.getId())
                .userId(plannedWorkout.getUserId())
                .workout(workoutTransformer.transformToDTO(plannedWorkout.getWorkout()))
                .plannedDate(plannedWorkout.getPlannedDate())
                .createdAt(plannedWorkout.getCreatedAt())
                .updatedAt(plannedWorkout.getUpdatedAt())
                .build();
    }
}
