package com.cyctius.service.impl;

import com.cyctius.dto.SharedWorkoutDTO;
import com.cyctius.entity.Workout;
import com.cyctius.service.SharedWorkoutTransformer;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SharedWorkoutTransformerImpl implements SharedWorkoutTransformer {
    @Override
    public SharedWorkoutDTO transformFromEntity(final Workout workout) {
        if (Objects.isNull(workout)) {
            return null;
        }

        return SharedWorkoutDTO.builder()
                .name(workout.getName())
                .description(workout.getDescription())
                .intervalsJson(workout.getIntervalsJson())
                .build();
    }
}
