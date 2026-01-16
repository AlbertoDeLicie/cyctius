package com.cyctius.service.impl;

import org.springframework.stereotype.Service;

import com.cyctius.dto.TrainingSessionMetadataDTO;
import com.cyctius.entity.TrainingSessionMetadata;
import com.cyctius.service.TrainingSessionMetadataTransformer;

@Service
public class TrainingSessionMetadataTransformerImpl implements TrainingSessionMetadataTransformer {

    @Override
    public TrainingSessionMetadataDTO transformToDTO(TrainingSessionMetadata trainingSessionMetadata) {
        if (trainingSessionMetadata == null) {
            return null;
        }

        return TrainingSessionMetadataDTO.builder()
                .id(trainingSessionMetadata.getId())
                .trainingSessionId(trainingSessionMetadata.getTrainingSessionId())
                .tss(trainingSessionMetadata.getTss())
                .intensityFactor(trainingSessionMetadata.getIntensityFactor())
                .normalizedPower(trainingSessionMetadata.getNormalizedPower())
                .averagePower(trainingSessionMetadata.getAveragePower())
                .averageHeartRate(trainingSessionMetadata.getAverageHeartRate())
                .durationSeconds(trainingSessionMetadata.getDurationSeconds())
                .startedAt(trainingSessionMetadata.getStartedAt())
                .completedAt(trainingSessionMetadata.getCompletedAt())
                .createdAt(trainingSessionMetadata.getCreatedAt())
                .updatedAt(trainingSessionMetadata.getUpdatedAt())
                .build();
    }

    @Override
    public TrainingSessionMetadata transformToEntity(TrainingSessionMetadataDTO trainingSessionMetadataDTO) {
        if (trainingSessionMetadataDTO == null) {
            return null;
        }

        return TrainingSessionMetadata.builder()
                .id(trainingSessionMetadataDTO.getId())
                .trainingSessionId(trainingSessionMetadataDTO.getTrainingSessionId())
                .tss(trainingSessionMetadataDTO.getTss())
                .intensityFactor(trainingSessionMetadataDTO.getIntensityFactor())
                .normalizedPower(trainingSessionMetadataDTO.getNormalizedPower())
                .averagePower(trainingSessionMetadataDTO.getAveragePower())
                .averageHeartRate(trainingSessionMetadataDTO.getAverageHeartRate())
                .durationSeconds(trainingSessionMetadataDTO.getDurationSeconds())
                .startedAt(trainingSessionMetadataDTO.getStartedAt())
                .completedAt(trainingSessionMetadataDTO.getCompletedAt())
                .createdAt(trainingSessionMetadataDTO.getCreatedAt())
                .updatedAt(trainingSessionMetadataDTO.getUpdatedAt())
                .build();
    }
}
