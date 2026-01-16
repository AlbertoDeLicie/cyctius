package com.cyctius.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingSessionMetadataDTO {
    private String id;
    private String trainingSessionId;
    private Integer tss;
    private Double intensityFactor;
    private Integer normalizedPower;
    private Integer averagePower;
    private Integer averageHeartRate;
    private Integer durationSeconds;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
