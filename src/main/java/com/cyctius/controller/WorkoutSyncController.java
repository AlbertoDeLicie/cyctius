package com.cyctius.controller;

import com.cyctius.dto.SyncLocalWorkoutsRequestDTO;
import com.cyctius.dto.WorkoutDTO;
import com.cyctius.service.WorkoutService;
import com.cyctius.service.WorkoutSyncService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkoutSyncController {
    private final WorkoutSyncService workoutSyncService;
    private final WorkoutService workoutService;

    @PostMapping("/workouts")
    ResponseEntity<List<WorkoutDTO>> syncWorkouts(@RequestBody @Valid SyncLocalWorkoutsRequestDTO request) {
        List<WorkoutDTO> syncedWorkouts = workoutSyncService.syncWorkouts(request);
        return ResponseEntity.ok(syncedWorkouts);
    }

    @PostMapping("/workout")
    ResponseEntity<WorkoutDTO> syncWorkout(@RequestBody @Valid WorkoutDTO workoutDTO) {
        WorkoutDTO syncedWorkout = workoutSyncService.syncWorkout(workoutDTO);
        return ResponseEntity.ok(syncedWorkout);
    }

    @DeleteMapping("/workout/delete/{id}")
    ResponseEntity<Void> deleteWorkout(@PathVariable("id") @NotNull String id) {
        workoutService.softDeleteWorkout(id);
        return ResponseEntity.noContent().build();
    }
}
