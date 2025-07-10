package com.cyctius.service.impl;

import com.cyctius.dto.WorkoutDTO;
import com.cyctius.entity.Workout;
import com.cyctius.service.WorkoutTransformer;
import org.springframework.stereotype.Service;

@Service
public class WorkoutTransformerImpl implements WorkoutTransformer {
    @Override
    public Workout transformToEntity(final WorkoutDTO workoutDTO) {
        if (workoutDTO == null) {
            return null;
        }

        return Workout.builder()
                .id(workoutDTO.getId())
                .authorId(workoutDTO.getAuthorId())
                .name(workoutDTO.getName())
                .description(workoutDTO.getDescription())
                .isSoftDeleted(workoutDTO.getIsSoftDeleted())
                .intervalsJson(workoutDTO.getIntervalsJson())
                .createdAt(workoutDTO.getCreatedAt())
                .updatedAt(workoutDTO.getUpdatedAt())
                .build();
    }

    @Override
    public WorkoutDTO transformToDTO(final Workout workout) {
        if (workout == null) {
            return null;
        }

        return WorkoutDTO.builder()
                .id(workout.getId())
                .authorId(workout.getAuthorId())
                .name(workout.getName())
                .description(workout.getDescription())
                .isSoftDeleted(workout.getIsSoftDeleted())
                .intervalsJson(workout.getIntervalsJson())
                .createdAt(workout.getCreatedAt())
                .updatedAt(workout.getUpdatedAt())
                .build();
    }
}
