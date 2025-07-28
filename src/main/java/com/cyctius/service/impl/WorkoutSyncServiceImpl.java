package com.cyctius.service.impl;

import com.cyctius.dto.SyncLocalWorkoutsRequestDTO;
import com.cyctius.dto.WorkoutDTO;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.service.WorkoutSyncService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WorkoutSyncServiceImpl implements WorkoutSyncService {

    private final WorkoutServiceImpl workoutService;

    @Override
    public List<WorkoutDTO> syncWorkouts(final SyncLocalWorkoutsRequestDTO syncLocalWorkoutsRequestDTO) {
        if (Objects.isNull(syncLocalWorkoutsRequestDTO)) {
            throw new BadRequestException("sync.error.sync-request-cannot-be-null");
        }

        val serverSideWorkoutDTOs = workoutService.getAllWorkouts();

        return workoutService.insertWorkouts(mergeWorkouts(syncLocalWorkoutsRequestDTO.getLocalWorkouts(), serverSideWorkoutDTOs))
                .stream()
                .filter(w -> !w.getIsSoftDeleted())
                .toList();
    }

    @Override
    public WorkoutDTO syncWorkout(final WorkoutDTO workoutDTO) {
        if (Objects.isNull(workoutDTO)) {
            throw new BadRequestException("sync.error.workout-cannot-be-null");
        }

        return workoutService.insertWorkout(workoutDTO);
    }

    private List<WorkoutDTO> mergeWorkouts(final List<WorkoutDTO> local, final List<WorkoutDTO> server) {
        if (local.isEmpty()) {
            return server;
        }

        if (server.isEmpty()) {
            return local;
        }

        return Stream.concat(local.stream(), server.stream())
                .collect(Collectors.toMap(
                        WorkoutDTO::getId,
                        Function.identity(),
                        (w1, w2) -> w1.getUpdatedAt().isAfter(w2.getUpdatedAt()) ? w1 : w2
                ))
                .values().stream()
                .toList();
    }
}
