package com.cyctius.core.service;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.dto.WorkoutMetadataDTO;

public interface WorkoutMetadataCalculator {
    WorkoutMetadataDTO calculateMetadata(WorkoutModel workout, Boolean forceRecalculate);
}
