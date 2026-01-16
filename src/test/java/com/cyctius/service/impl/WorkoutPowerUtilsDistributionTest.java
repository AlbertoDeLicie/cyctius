package com.cyctius.service.impl;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.core.service.impl.WorkoutPowerUtilsImpl;
import com.cyctius.core.enums.PowerZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkoutPowerUtilsDistributionTest {

    private WorkoutPowerUtilsImpl workoutPowerUtils;

    @BeforeEach
    void setUp() {
        workoutPowerUtils = new WorkoutPowerUtilsImpl();
    }

    @Test
    void testCalculateDistribution_nullWorkout_shouldReturnEmptyMap() {
        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateDistribution_nullIntervals_shouldReturnEmptyMap() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(null);

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateDistribution_emptyIntervals_shouldReturnEmptyMap() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(new ArrayList<>());

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateDistribution_singleIntervalZ2_shouldReturnCorrectDistribution() {
        // Given
        // Intensity 60 is in Z2_ENDURANCE (55-75), duration 300 seconds
        SingleInterval interval = new SingleInterval(60, 90, true, 300);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertEquals(300, result.get(PowerZone.Z2_ENDURANCE));
        assertEquals(0, result.get(PowerZone.Z1_ACTIVE_RECOVERY));
        assertEquals(0, result.get(PowerZone.Z3_TEMPO));
    }

    @Test
    void testCalculateDistribution_multipleSingleIntervalsDifferentZones_shouldSumCorrectly() {
        // Given
        // Z2: 60 intensity, 200 seconds
        // Z4: 100 intensity, 300 seconds
        // Z1: 40 intensity, 100 seconds
        SingleInterval z2 = new SingleInterval(60, 90, true, 200);
        SingleInterval z4 = new SingleInterval(100, 90, true, 300);
        SingleInterval z1 = new SingleInterval(40, 90, true, 100);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(z2, z4, z1));

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertEquals(100, result.get(PowerZone.Z1_ACTIVE_RECOVERY));
        assertEquals(200, result.get(PowerZone.Z2_ENDURANCE));
        assertEquals(300, result.get(PowerZone.Z4_THRESHOLD));
        assertEquals(0, result.get(PowerZone.Z3_TEMPO));
    }

    @Test
    void testCalculateDistribution_rampInterval_shouldUseFromIntensity() {
        // Given
        // Ramp from 50 (Z1) to 80 (Z3), but uses from intensity (50) -> Z1
        RampInterval ramp = new RampInterval(50, 80, 90, true, 600);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(ramp));

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertEquals(600, result.get(PowerZone.Z1_ACTIVE_RECOVERY));
        assertEquals(0, result.get(PowerZone.Z3_TEMPO));
    }

    @Test
    void testCalculateDistribution_repeatInterval_shouldSumWorkAndRest() {
        // Given
        // Work: 90 intensity (Z3), 60 seconds, 3 repeats = 180 seconds
        // Rest: 30 intensity (Z1), 20 seconds, 3 repeats = 60 seconds
        SingleInterval work = new SingleInterval(90, 90, true, 60);
        SingleInterval rest = new SingleInterval(30, 90, true, 20);
        RepeatInterval repeat = new RepeatInterval(rest, work, 3);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(repeat));

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertEquals(60, result.get(PowerZone.Z1_ACTIVE_RECOVERY));
        assertEquals(180, result.get(PowerZone.Z3_TEMPO));
    }

    @Test
    void testCalculateDistribution_mixedIntervals_shouldCalculateAllZones() {
        // Given
        SingleInterval z1 = new SingleInterval(40, 90, true, 100);
        SingleInterval z5 = new SingleInterval(110, 90, true, 200);
        RampInterval ramp = new RampInterval(50, 70, 90, true, 300);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(z1, z5, ramp));

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertEquals(400, result.get(PowerZone.Z1_ACTIVE_RECOVERY)); // Total Z1: 100 + 300 (ramp uses from)
        assertEquals(200, result.get(PowerZone.Z5_VO2MAX));
        assertEquals(0, result.get(PowerZone.Z2_ENDURANCE));
    }

    @Test
    void testCalculateDistribution_allZonesCovered_shouldHaveAllZonesInMap() {
        // Given
        SingleInterval z1 = new SingleInterval(40, 90, true, 60);
        SingleInterval z2 = new SingleInterval(60, 90, true, 60);
        SingleInterval z3 = new SingleInterval(80, 90, true, 60);
        SingleInterval z4 = new SingleInterval(100, 90, true, 60);
        SingleInterval z5 = new SingleInterval(110, 90, true, 60);
        SingleInterval z6 = new SingleInterval(130, 90, true, 60);
        SingleInterval z7 = new SingleInterval(160, 90, true, 60);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(z1, z2, z3, z4, z5, z6, z7));

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertEquals(60, result.get(PowerZone.Z1_ACTIVE_RECOVERY));
        assertEquals(60, result.get(PowerZone.Z2_ENDURANCE));
        assertEquals(60, result.get(PowerZone.Z3_TEMPO));
        assertEquals(60, result.get(PowerZone.Z4_THRESHOLD));
        assertEquals(60, result.get(PowerZone.Z5_VO2MAX));
        assertEquals(60, result.get(PowerZone.Z6_ANEROBIC_CAPACITY));
        assertEquals(60, result.get(PowerZone.Z7_NEUROMUSCULAR_POWER));
    }

    @Test
    void testCalculateDistribution_boundaryIntensities_shouldClassifyCorrectly() {
        // Given
        // Testing boundary values: 54 (Z1), 55 (Z2), 75 (Z2), 76 (Z3), 90 (Z3), 91 (Z4)
        SingleInterval z1Boundary = new SingleInterval(54, 90, true, 100);
        SingleInterval z2Start = new SingleInterval(55, 90, true, 100);
        SingleInterval z2End = new SingleInterval(75, 90, true, 100);
        SingleInterval z3Start = new SingleInterval(76, 90, true, 100);
        SingleInterval z3End = new SingleInterval(90, 90, true, 100);
        SingleInterval z4Start = new SingleInterval(91, 90, true, 100);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(z1Boundary, z2Start, z2End, z3Start, z3End, z4Start));

        // When
        Map<PowerZone, Integer> result = workoutPowerUtils.calculateDistribution(workout);

        // Then
        assertNotNull(result);
        assertEquals(100, result.get(PowerZone.Z1_ACTIVE_RECOVERY));
        assertEquals(200, result.get(PowerZone.Z2_ENDURANCE));
        assertEquals(200, result.get(PowerZone.Z3_TEMPO));
        assertEquals(100, result.get(PowerZone.Z4_THRESHOLD));
    }
}
