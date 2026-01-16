package com.cyctius.service.impl;

import org.springframework.stereotype.Service;

import com.cyctius.dto.WorkoutMetadataDTO;
import com.cyctius.entity.WorkoutMetadata;
import com.cyctius.service.WorkoutMetadataTransformer;

@Service
public class WorkoutMetadataTransformerImpl implements WorkoutMetadataTransformer {

    @Override
    public WorkoutMetadataDTO transformToDTO(WorkoutMetadata workoutMetadata) {
        if (workoutMetadata == null) {
            return null;
        }

        return WorkoutMetadataDTO.builder()
                .id(workoutMetadata.getId())
                .workoutId(workoutMetadata.getWorkoutId())
                .estimatedTss(workoutMetadata.getEstimatedTss())
                .trainingType(workoutMetadata.getTrainingType())
                .difficulty(workoutMetadata.getDifficulty())
                .intensityFactor(workoutMetadata.getIntensityFactor())
                .averageIntensity(workoutMetadata.getAverageIntensity())
                .durationSeconds(workoutMetadata.getDurationSeconds())
                .createdAt(workoutMetadata.getCreatedAt())
                .updatedAt(workoutMetadata.getUpdatedAt())
                .build();
    }

    @Override
    public WorkoutMetadata transformToEntity(WorkoutMetadataDTO workoutMetadataDTO) {
        if (workoutMetadataDTO == null) {
            return null;
        }

        return WorkoutMetadata.builder()
                .id(workoutMetadataDTO.getId())
                .workoutId(workoutMetadataDTO.getWorkoutId())
                .estimatedTss(workoutMetadataDTO.getEstimatedTss())
                .trainingType(workoutMetadataDTO.getTrainingType())
                .difficulty(workoutMetadataDTO.getDifficulty())
                .intensityFactor(workoutMetadataDTO.getIntensityFactor())
                .averageIntensity(workoutMetadataDTO.getAverageIntensity())
                .durationSeconds(workoutMetadataDTO.getDurationSeconds())
                .createdAt(workoutMetadataDTO.getCreatedAt())
                .updatedAt(workoutMetadataDTO.getUpdatedAt())
                .build();
    }
}
