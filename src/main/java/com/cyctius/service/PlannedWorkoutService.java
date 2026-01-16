package com.cyctius.service;

import com.cyctius.dto.PlannedWorkoutDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PlannedWorkoutService {
    PlannedWorkoutDTO getPlannedWorkoutById(String id);
    Page<PlannedWorkoutDTO> getPlannedWorkoutsByTimeRange(LocalDateTime start, LocalDateTime end, Pageable pageable);
    PlannedWorkoutDTO insertPlannedWorkout(PlannedWorkoutDTO plannedWorkoutDTO);
    PlannedWorkoutDTO updatePlannedWorkout(String id, PlannedWorkoutDTO plannedWorkoutDTO);
    void deletePlannedWorkout(String id);
}
