package com.cyctius.controller;

import com.cyctius.dto.PlannedWorkoutDTO;
import com.cyctius.service.PlannedWorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/planned-workouts")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlannedWorkoutController {
    private final PlannedWorkoutService plannedWorkoutService;

    @GetMapping("/{id}")
    public ResponseEntity<PlannedWorkoutDTO> getPlannedWorkoutById(@PathVariable String id) {
        return ResponseEntity.ok(plannedWorkoutService.getPlannedWorkoutById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PlannedWorkoutDTO>> getPlannedWorkoutsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable) {
        return ResponseEntity.ok(plannedWorkoutService.getPlannedWorkoutsByTimeRange(start, end, pageable));
    }

    @PostMapping
    public ResponseEntity<PlannedWorkoutDTO> insertPlannedWorkout(@RequestBody PlannedWorkoutDTO plannedWorkoutDTO) {
        return ResponseEntity.ok(plannedWorkoutService.insertPlannedWorkout(plannedWorkoutDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlannedWorkoutDTO> updatePlannedWorkout(@PathVariable String id, @RequestBody PlannedWorkoutDTO plannedWorkoutDTO) {
        return ResponseEntity.ok(plannedWorkoutService.updatePlannedWorkout(id, plannedWorkoutDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlannedWorkout(@PathVariable String id) {
        plannedWorkoutService.deletePlannedWorkout(id);
        return ResponseEntity.noContent().build();
    }
}
