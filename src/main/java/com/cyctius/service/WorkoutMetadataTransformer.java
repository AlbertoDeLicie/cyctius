package com.cyctius.service;

import com.cyctius.dto.WorkoutMetadataDTO;
import com.cyctius.entity.WorkoutMetadata;

public interface WorkoutMetadataTransformer {
    WorkoutMetadataDTO transformToDTO(WorkoutMetadata workoutMetadata);
    WorkoutMetadata transformToEntity(WorkoutMetadataDTO workoutMetadataDTO);
}
