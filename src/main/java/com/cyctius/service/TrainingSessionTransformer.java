package com.cyctius.service;

import com.cyctius.dto.TrainingSessionDTO;
import com.cyctius.entity.TrainingSession;

/**
 * Transformer for converting between TrainingSession entity and DTO.
 */
public interface TrainingSessionTransformer {
    
    /**
     * Transform TrainingSession entity to DTO.
     *
     * @param trainingSession the entity
     * @return the DTO
     */
    TrainingSessionDTO transformToDTO(TrainingSession trainingSession);
    
    /**
     * Transform TrainingSession DTO to entity.
     *
     * @param trainingSessionDTO the DTO
     * @return the entity
     */
    TrainingSession transformToEntity(TrainingSessionDTO trainingSessionDTO);
}

