package com.cyctius.service;

import com.cyctius.dto.WorkoutDTO;
import com.cyctius.service.impl.WorkoutSyncServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WorkoutSyncServiceImplTest {
    @Mock
    private WorkoutSyncServiceImpl workoutService;

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

        return new WorkoutDTO(
                randomId,
                "authorId",
                availableWorkoutNames.get(randomIndex),
                availableWorkoutDescriptions.get(randomIndex),
                isSoftDeleted,
                "[]", // intervalsJson
                LocalDateTime.now(),  // createdAt
                LocalDateTime.now()   // updatedAt
        );
    }

    WorkoutDTO createRandomWorkoutDTO(
            String id,
            String name,
            Boolean isSoftDeleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        int randomIndex = (int) (Math.random() * availableWorkoutNames.size());

        return new WorkoutDTO(
                id,
                "authorId",
                name,
                availableWorkoutDescriptions.get(randomIndex),
                isSoftDeleted,
                "[]", // intervalsJson,
                createdAt,  // createdAt
                updatedAt   // updatedAt
        );
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
}
