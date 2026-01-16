package com.cyctius.service;

import com.cyctius.dto.TrainingSessionMetadataDTO;
import com.cyctius.entity.TrainingSessionMetadata;

public interface TrainingSessionMetadataTransformer {
    TrainingSessionMetadataDTO transformToDTO(TrainingSessionMetadata trainingSessionMetadata);
    TrainingSessionMetadata transformToEntity(TrainingSessionMetadataDTO trainingSessionMetadataDTO);
}
