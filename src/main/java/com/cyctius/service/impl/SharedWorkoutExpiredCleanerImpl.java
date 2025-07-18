package com.cyctius.service.impl;

import com.cyctius.service.SharedWorkoutExpiredCleaner;
import com.cyctius.service.SharedWorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SharedWorkoutExpiredCleanerImpl implements SharedWorkoutExpiredCleaner {

    private final SharedWorkoutService sharedWorkoutService;

    @Override
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void cleanExpiredSharedWorkouts() {
        sharedWorkoutService.cleanExpiredSharedWorkouts();
    }
}
