package com.cyctius.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlannedWorkoutDTO {
    private String id;
    private String userId;
    private WorkoutDTO workout;
    private LocalDateTime plannedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
