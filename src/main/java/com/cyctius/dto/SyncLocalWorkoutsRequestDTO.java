package com.cyctius.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncLocalWorkoutsRequestDTO {
    @NotNull(message = "error.sync.local-workouts.notnull")
    private List<WorkoutDTO> localWorkouts;
}
