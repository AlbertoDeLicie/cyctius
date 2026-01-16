package com.cyctius.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.cyctius.core.model.intervals.Interval;
import com.cyctius.enums.WorkoutVisibility;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutDTO {
    private String id;
    private String authorId;
    @NotBlank(message = "error.workout.name.notblank")
    private String name;
    private String description;
    private WorkoutVisibility visibility;
    private Boolean isSoftDeleted;
    @NotNull(message = "error.workout.intervals.notnull")
    private List<Interval> intervals;
    private WorkoutMetadataDTO metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}