package com.cyctius.service.impl;

import com.cyctius.core.model.intervals.Interval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.dto.SyncLocalWorkoutsRequestDTO;
import com.cyctius.dto.WorkoutDTO;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.service.WorkoutService;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkoutSyncServiceImplTest {
    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private WorkoutSyncServiceImpl workoutSyncService;

    private final List<String> availableWorkoutNames = List.of(
            "Workout A",
            "Workout B",
            "Workout C",
            "Workout D",
            "Workout E",
            "Workout F",
            "Workout G"
    );

    private final List<String> availableWorkoutDescriptions = List.of(
            "Description A",
            "Description B",
            "Description C",
            "Description D",
            "Description E",
            "Description F",
            "Description G"
    );

    WorkoutDTO createRandomWorkoutDTO(
            Boolean withId,
            Boolean isSoftDeleted
    ) {
        int randomIndex = (int) (Math.random() * availableWorkoutNames.size());
        val randomId = withId ? UUID.randomUUID().toString() : null;

        return WorkoutDTO.builder()
                .id(randomId)
                .authorId("authorId")
                .name(availableWorkoutNames.get(randomIndex))
                .description(availableWorkoutDescriptions.get(randomIndex))
                .isSoftDeleted(isSoftDeleted)
                .intervals(java.util.Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    WorkoutDTO createRandomWorkoutDTO(
            String id,
            String name,
            Boolean isSoftDeleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return WorkoutDTO.builder()
                .id(id)
                .authorId("authorId")
                .name(name)
                .description("description")
                .isSoftDeleted(isSoftDeleted)
                .intervals(java.util.Collections.emptyList())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    // Синхронизация тренировок, когда локальные и серверные пусты, без soft-deleted тренировок.
    @Test
    void testMergeWorkouts_allEmpty() {
        // Given
        val localWorkouts = List.<WorkoutDTO>of();
        val serverWorkouts = List.<WorkoutDTO>of();

        // When
        List<WorkoutDTO> result = workoutSyncService.mergeWorkouts(localWorkouts, serverWorkouts);

        // Then
        assertEquals(0, result.size());
    }

    @Test
    void testMergeWorkouts_nullInputs() {
        assertEquals(0, workoutSyncService.mergeWorkouts(null, null).size());
        assertEquals(0, workoutSyncService.mergeWorkouts(List.of(), null).size());
        
        val server = List.of(createRandomWorkoutDTO(true, false));
        assertEquals(server, workoutSyncService.mergeWorkouts(null, server));
        
        val local = List.of(createRandomWorkoutDTO(false, false));
        assertEquals(local, workoutSyncService.mergeWorkouts(local, null));
    }

    // Синхронизация локальных тренировок, когда сервер пуст, без soft-deleted тренировок.
    // Тренировки локальные, поэтому id у них нет.
    @Test
    void testMergeWorkouts_shouldReturnLocalWhenServerIsEmpty() {
        // Given
        val localWorkouts = List.of(
                createRandomWorkoutDTO(false, false),
                createRandomWorkoutDTO(false, false)
        );

        val serverWorkouts = List.<WorkoutDTO>of();

        // When
        List<WorkoutDTO> result = workoutSyncService.mergeWorkouts(localWorkouts, serverWorkouts);

        // Then
        assertEquals(localWorkouts, result);
    }

    // Синхронизация серверных тренировок, когда локальные пусты, без soft-deleted тренировок.
    // Тренировки серверные, поэтому id у них есть.
    @Test
    void testMergeWorkouts_shouldReturnServerWhenLocalIsEmpty() {
        // Given
        val serverWorkouts = List.of(
                createRandomWorkoutDTO(true, false),
                createRandomWorkoutDTO(true, false)
        );

        val localWorkouts = List.<WorkoutDTO>of();

        // When
        List<WorkoutDTO> result = workoutSyncService.mergeWorkouts(localWorkouts, serverWorkouts);

        // Then
        assertEquals(serverWorkouts, result);
    }

    // Синхронизация тренировок, когда локальные и серверные не пусты, без soft-deleted тренировок.
    @Test
    void testMergeWorkouts_shouldMergeLocalAndServerWorkouts() {
        // Given
        val localWorkouts = List.of(
                createRandomWorkoutDTO(
                        null,
                        "Local Workout 1",
                        false,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ),
                createRandomWorkoutDTO(
                        null,
                        "Local Workout 2",
                        false,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        val serverWorkouts = List.of(
                createRandomWorkoutDTO(true, false),
                createRandomWorkoutDTO(true, false)
        );

        // When
        List<WorkoutDTO> result = workoutSyncService.mergeWorkouts(localWorkouts, serverWorkouts);

        // Then
        assertEquals(4, result.size());

        // первыми идут серверные тренировки
        val concat = Stream.concat(
                serverWorkouts.stream(),
                localWorkouts.stream()
        ).toList();

        val sortedConcat = concat.stream()
                .sorted(Comparator.comparing(WorkoutDTO::getName))
                .toList();

        for (int i = 0; i < result.size(); i++) {
            assertEquals(sortedConcat.get(i).getName(), result.get(i).getName());
            assertEquals(sortedConcat.get(i).getDescription(), result.get(i).getDescription());
            assertEquals(sortedConcat.get(i).getIsSoftDeleted(), result.get(i).getIsSoftDeleted());
        }
    }

    // Синхронизация тренировок, когда локальная тренировка свежее серверной, без soft-deleted тренировок.
    @Test
    void testMergeWorkouts_shouldPreferLocalWhenUpdatedAtIsNewer() {

        val nowMinusOneDay = LocalDateTime.now().minusDays(1);

        val localWorkouts = List.of(
                createRandomWorkoutDTO("1", "new name 1", false, nowMinusOneDay, LocalDateTime.now()),
                createRandomWorkoutDTO("2", "new name 2", false, nowMinusOneDay, LocalDateTime.now())
        );

        val serverWorkouts = List.of(
                createRandomWorkoutDTO("1", "old name 1", false, nowMinusOneDay, LocalDateTime.now().minusDays(3)),
                createRandomWorkoutDTO("2", "old name 2", false, nowMinusOneDay, LocalDateTime.now().minusDays(3))
        );

        // When
        val result = workoutSyncService.mergeWorkouts(
                localWorkouts,
                serverWorkouts
        );

        // Then
        assertEquals(2, result.size());

        for (val workout : result) {
            assertTrue(workout.getName().contains("new name"));
        }
    }

    @Test
    void testMergeWorkouts_shouldHandleNullUpdatedAt() {
        val id = "1";
        val workoutWithNullUpdate = createRandomWorkoutDTO(id, "Null Update", false, LocalDateTime.now(), null);
        val workoutWithUpdate = createRandomWorkoutDTO(id, "With Update", false, LocalDateTime.now(), LocalDateTime.now());

        // Null first
        val result1 = workoutSyncService.mergeWorkouts(List.of(workoutWithNullUpdate), List.of(workoutWithUpdate));
        assertEquals("With Update", result1.get(0).getName());

        // Null second
        val result2 = workoutSyncService.mergeWorkouts(List.of(workoutWithUpdate), List.of(workoutWithNullUpdate));
        assertEquals("With Update", result2.get(0).getName());
        
        // Both null
        val result3 = workoutSyncService.mergeWorkouts(List.of(workoutWithNullUpdate), List.of(workoutWithNullUpdate));
        assertEquals("Null Update", result3.get(0).getName());
    }

    @Test
    void testMergeWorkout_localHaveDeletedWorkouts() {

        val nowMinusOneDay = LocalDateTime.now().minusDays(1);

        // Given
        val localWorkouts = List.of(
                createRandomWorkoutDTO("1", "new name 1", true, nowMinusOneDay, LocalDateTime.now()),
                createRandomWorkoutDTO("2", "new name 2", true, nowMinusOneDay, LocalDateTime.now())
        );

        val serverWorkouts = List.of(
                createRandomWorkoutDTO("1", "old name 1", false, nowMinusOneDay, LocalDateTime.now().minusDays(3)),
                createRandomWorkoutDTO("2", "old name 2", false, nowMinusOneDay, LocalDateTime.now().minusDays(3))
        );

        // When
        List<WorkoutDTO> result = workoutSyncService.mergeWorkouts(localWorkouts, serverWorkouts);

        val deletedCount = result.stream()
                .filter(WorkoutDTO::getIsSoftDeleted)
                .count();

        // Then
        assertEquals(2, result.size());
        assertEquals(2, deletedCount);
    }

    @Test
    void testMergeWorkouts_serverNewerButSoftDeleted_shouldPreserveSoftDelete() {
        val id = UUID.randomUUID().toString();

        val local = createRandomWorkoutDTO(id, "Workout", false, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        val server = createRandomWorkoutDTO(id, "Workout", true, LocalDateTime.now().minusDays(2), LocalDateTime.now());

        List<WorkoutDTO> result = workoutSyncService.mergeWorkouts(List.of(local), List.of(server));

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsSoftDeleted());
    }

    @Test
    void testMergeWorkouts_withMultipleNullIds_shouldIncludeAll() {
        val workout1 = createRandomWorkoutDTO(null, "Null 1", false, LocalDateTime.now(), LocalDateTime.now());
        val workout2 = createRandomWorkoutDTO(null, "Null 2", false, LocalDateTime.now(), LocalDateTime.now());

        List<WorkoutDTO> result = workoutSyncService.mergeWorkouts(List.of(workout1, workout2), List.of());

        assertEquals(2, result.size());
        assertTrue(result.contains(workout1));
        assertTrue(result.contains(workout2));
    }

    @Test
    void testSyncWorkouts_nullRequest_shouldThrowException() {
        assertThrows(BadRequestException.class, () -> workoutSyncService.syncWorkouts(null));
    }

    @Test
    void testSyncWorkouts_nullLocalWorkouts_shouldThrowException() {
        val request = new SyncLocalWorkoutsRequestDTO();
        assertThrows(BadRequestException.class, () -> workoutSyncService.syncWorkouts(request));
    }

    @Test
    void testSyncWorkouts_duplicateIds_shouldThrowException() {
        val id = UUID.randomUUID().toString();
        val workout1 = createRandomWorkoutDTO(id, "W1", false, LocalDateTime.now(), LocalDateTime.now());
        val workout2 = createRandomWorkoutDTO(id, "W2", false, LocalDateTime.now(), LocalDateTime.now());

        val request = SyncLocalWorkoutsRequestDTO.builder()
                .localWorkouts(List.of(workout1, workout2))
                .build();

        assertThrows(BadRequestException.class, () -> workoutSyncService.syncWorkouts(request));
    }

    @Test
    void testSyncWorkouts_success() {
        // Given
        val localWorkout = createRandomWorkoutDTO(null, "Local", false, LocalDateTime.now(), LocalDateTime.now());
        val serverWorkout = createRandomWorkoutDTO(UUID.randomUUID().toString(), "Server", false, LocalDateTime.now(), LocalDateTime.now());

        val request = SyncLocalWorkoutsRequestDTO.builder()
                .localWorkouts(List.of(localWorkout))
                .build();

        when(workoutService.getAllWorkouts()).thenReturn(List.of(serverWorkout));
        when(workoutService.insertWorkouts(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        val result = workoutSyncService.syncWorkouts(request);

        // Then
        assertEquals(2, result.size());
        verify(workoutService).getAllWorkouts();
        verify(workoutService).insertWorkouts(anyList());
    }

    @Test
    void testSyncWorkout_nullWorkout_shouldThrowException() {
        assertThrows(BadRequestException.class, () -> workoutSyncService.syncWorkout(null));
    }

    @Test
    void testSyncWorkout_success() {
        // Given
        val workoutDTO = createRandomWorkoutDTO(true, false);
        when(workoutService.insertWorkout(workoutDTO)).thenReturn(workoutDTO);

        // When
        val result = workoutSyncService.syncWorkout(workoutDTO);

        // Then
        assertEquals(workoutDTO, result);
        verify(workoutService).insertWorkout(workoutDTO);
    }

    @Test
    void testMergeWorkouts_shouldUpdateIntervalsWhenLocalIsNewer() {
        val id = UUID.randomUUID().toString();
        val serverIntervals = List.<Interval>of(new SingleInterval(100, 90, true, 600));
        val localIntervals = List.<Interval>of(new SingleInterval(150, 95, true, 300));

        val server = createRandomWorkoutDTO(id, "Workout", false, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        server.setIntervals(serverIntervals);

        val local = createRandomWorkoutDTO(id, "Workout", false, LocalDateTime.now().minusDays(1), LocalDateTime.now());
        local.setIntervals(localIntervals);

        val result = workoutSyncService.mergeWorkouts(List.of(local), List.of(server));

        assertEquals(1, result.size());
        assertEquals(localIntervals, result.get(0).getIntervals());
        assertEquals(150, ((SingleInterval) result.get(0).getIntervals().get(0)).getTargetIntensity());
    }

    @Test
    void testMergeWorkouts_shouldPreserveServerIntervalsWhenServerIsNewer() {
        val id = UUID.randomUUID().toString();
        val serverIntervals = List.<Interval>of(new SingleInterval(100, 90, true, 600));
        val localIntervals = List.<Interval>of(new SingleInterval(150, 95, true, 300));

        val server = createRandomWorkoutDTO(id, "Workout", false, LocalDateTime.now().minusDays(1), LocalDateTime.now());
        server.setIntervals(serverIntervals);

        val local = createRandomWorkoutDTO(id, "Workout", false, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1));
        local.setIntervals(localIntervals);

        val result = workoutSyncService.mergeWorkouts(List.of(local), List.of(server));

        assertEquals(1, result.size());
        assertEquals(serverIntervals, result.get(0).getIntervals());
        assertEquals(100, ((SingleInterval) result.get(0).getIntervals().get(0)).getTargetIntensity());
    }

    @Test
    void testMergeWorkouts_shouldPreserveIntervalsForNewWorkouts() {
        val intervals = List.<Interval>of(new SingleInterval(200, 100, true, 120));
        val newWorkout = createRandomWorkoutDTO(null, "New Workout", false, LocalDateTime.now(), LocalDateTime.now());
        newWorkout.setIntervals(intervals);

        val result = workoutSyncService.mergeWorkouts(List.of(newWorkout), List.of());

        assertEquals(1, result.size());
        assertEquals(intervals, result.get(0).getIntervals());
    }
}
