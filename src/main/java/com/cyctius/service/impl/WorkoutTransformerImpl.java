package com.cyctius.service.impl;

import com.cyctius.dto.WorkoutDTO;
import com.cyctius.entity.Workout;
import com.cyctius.service.WorkoutMetadataTransformer;
import com.cyctius.service.WorkoutTransformer;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkoutTransformerImpl implements WorkoutTransformer {
    private final WorkoutMetadataTransformer workoutMetadataTransformer;

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
                .visibility(workoutDTO.getVisibility())
                .isSoftDeleted(workoutDTO.getIsSoftDeleted())
                .intervals(workoutDTO.getIntervals())
                .metadata(workoutMetadataTransformer.transformToEntity(workoutDTO.getMetadata()))
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
                .visibility(workout.getVisibility())
                .isSoftDeleted(workout.getIsSoftDeleted())
                .intervals(workout.getIntervals())
                .metadata(workoutMetadataTransformer.transformToDTO(workout.getMetadata()))
                .createdAt(workout.getCreatedAt())
                .updatedAt(workout.getUpdatedAt())
                .build();
    }
}
