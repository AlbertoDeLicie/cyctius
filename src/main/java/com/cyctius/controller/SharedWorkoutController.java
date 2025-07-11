package com.cyctius.controller;

import com.cyctius.service.SharedWorkoutService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/share")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SharedWorkoutController {

    private final SharedWorkoutService sharedWorkoutService;

    @PostMapping("/workout")
    ResponseEntity<String> shareWorkout(@RequestBody @NotNull final String workoutId) {
        val sharedId = sharedWorkoutService.shareWorkout(workoutId);
        return ResponseEntity.ok(sharedId);
    }

    @GetMapping("/workout/get/{sharedId}")
    ResponseEntity<?> getSharedWorkout(@PathVariable("sharedId") @NotNull final String sharedId) {
        val workoutDTO = sharedWorkoutService.getSharedWorkout(sharedId);
        return ResponseEntity.ok(workoutDTO);
    }
}
