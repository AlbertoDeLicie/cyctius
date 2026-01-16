package com.cyctius.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.cyctius.enums.TrainingSessionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingSessionDTO {
    private String id;
    private String userId;
    private String workoutId;
    private TrainingSessionStatus status;
    private TrainingSessionMetadataDTO metadata;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

