package com.cyctius.service.impl;

import com.cyctius.dto.TrainingSessionDTO;
import com.cyctius.entity.TrainingSession;
import com.cyctius.service.TrainingSessionTransformer;
import com.cyctius.service.TrainingSessionMetadataTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TrainingSessionTransformerImpl implements TrainingSessionTransformer {

    private final TrainingSessionMetadataTransformer trainingSessionMetadataTransformer;

    @Override
    public TrainingSessionDTO transformToDTO(TrainingSession trainingSession) {
        if (trainingSession == null) {
            return null;
        }

        return TrainingSessionDTO.builder()
            .id(trainingSession.getId())
            .userId(trainingSession.getUserId())
            .workoutId(trainingSession.getWorkoutId())
            .completedAt(trainingSession.getCompletedAt())
            .metadata(trainingSessionMetadataTransformer.transformToDTO(trainingSession.getMetadata()))
            .status(trainingSession.getStatus())
            .createdAt(trainingSession.getCreatedAt())
            .updatedAt(trainingSession.getUpdatedAt())
            .build();
    }

    @Override
    public TrainingSession transformToEntity(TrainingSessionDTO trainingSessionDTO) {
        if (trainingSessionDTO == null) {
            return null;
        }

        return TrainingSession.builder()
            .id(trainingSessionDTO.getId())
            .userId(trainingSessionDTO.getUserId())
            .workoutId(trainingSessionDTO.getWorkoutId())
            .completedAt(trainingSessionDTO.getCompletedAt())
            .metadata(trainingSessionMetadataTransformer.transformToEntity(trainingSessionDTO.getMetadata()))
            .status(trainingSessionDTO.getStatus())
            .createdAt(trainingSessionDTO.getCreatedAt())
            .updatedAt(trainingSessionDTO.getUpdatedAt())
            .build();
    }
}

