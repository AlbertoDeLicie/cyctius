package com.cyctius.service.impl;

import com.cyctius.service.SoftDeletedWorkoutCleaner;
import com.cyctius.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SoftDeletedWorkoutCleanerImpl implements SoftDeletedWorkoutCleaner {

    private final WorkoutService workoutService;

    @Override
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanSoftDeletedWorkouts() {
        workoutService.cleanSoftDeletedWorkouts();
    }
}
