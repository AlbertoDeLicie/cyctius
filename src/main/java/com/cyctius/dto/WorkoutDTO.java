package com.cyctius.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutDTO {
    private String id;
    @NotBlank(message = "error.workout.authorId.notblank")
    private String authorId;
    @NotBlank(message = "error.workout.name.notblank")
    private String name;
    private String description;
    private Boolean isSoftDeleted;
    @NotNull(message = "error.workout.intervalsJson.notblank")
    private String intervalsJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}