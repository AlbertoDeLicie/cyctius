package com.cyctius.dto;

import java.time.LocalDateTime;

import com.cyctius.core.enums.WorkoutType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutMetadataDTO {
    private String id;
    private String workoutId;
    private Integer estimatedTss;
    private WorkoutType trainingType;
    private Double difficulty;
    private Double intensityFactor; // Range 0.0 and > 1.0
    private Integer averageIntensity; // Range 1-100 %
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
