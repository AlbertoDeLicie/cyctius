package com.cyctius.service;

import com.cyctius.dto.WorkoutDTO;

import java.util.List;

public interface WorkoutService {
    WorkoutDTO insertWorkout(WorkoutDTO workoutDTO);
    List<WorkoutDTO> insertWorkouts(List<WorkoutDTO> workoutDTOs);
    WorkoutDTO updateWorkout(WorkoutDTO workoutDTO);
    WorkoutDTO getWorkoutById(String id);
    void softDeleteWorkout(String id);
    List<WorkoutDTO> getAllWorkouts();
    List<WorkoutDTO> getWorkoutsPage(Integer page, Integer size);
    void cleanSoftDeletedWorkouts();
}
