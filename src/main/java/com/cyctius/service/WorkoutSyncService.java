package com.cyctius.service;

import com.cyctius.dto.SyncLocalWorkoutsRequestDTO;
import com.cyctius.dto.WorkoutDTO;

import java.util.List;

public interface WorkoutSyncService {
    List<WorkoutDTO> syncWorkouts(SyncLocalWorkoutsRequestDTO syncLocalWorkoutsRequestDTO);
    WorkoutDTO syncWorkout(WorkoutDTO workoutDTO);
}
