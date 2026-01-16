package com.cyctius.service.impl;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.core.service.impl.WorkoutPowerUtilsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkoutPowerUtilsAverageIntensityTest {

    private WorkoutPowerUtilsImpl workoutPowerUtils;

    @BeforeEach
    void setUp() {
        workoutPowerUtils = new WorkoutPowerUtilsImpl();
    }

    @Test
    void testCalculateAverageIntensity_nullWorkout_shouldReturnZero() {
        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(null);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateAverageIntensity_nullIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(null);

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateAverageIntensity_emptyIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(new ArrayList<>());

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateAverageIntensity_singleInterval_shouldReturnIntensity() {
        // Given
        SingleInterval interval = new SingleInterval(60, 90, true, 300);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(60, result);
    }

    @Test
    void testCalculateAverageIntensity_multipleSingleIntervals_shouldReturnAverage() {
        // Given
        // Average of (50 + 70 + 90) / 3 = 70
        SingleInterval interval1 = new SingleInterval(50, 90, true, 300);
        SingleInterval interval2 = new SingleInterval(70, 90, true, 600);
        SingleInterval interval3 = new SingleInterval(90, 90, true, 900);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval1, interval2, interval3));

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(70, result);
    }

    @Test
    void testCalculateAverageIntensity_rampInterval_shouldReturnAverageOfFromAndTo() {
        // Given
        // Ramp from 40 to 60, average = (40 + 60) / 2 = 50
        RampInterval ramp = new RampInterval(40, 60, 90, true, 600);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(ramp));

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(50, result);
    }

    @Test
    void testCalculateAverageIntensity_repeatInterval_shouldReturnWeightedAverage() {
        // Given
        // Work: 80 intensity, 60 seconds
        // Rest: 20 intensity, 20 seconds
        // Ratio = 60 / (60 + 20) = 0 (integer division)
        // Average = 80 * 0 + 20 * (1 - 0) = 20
        SingleInterval work = new SingleInterval(80, 90, true, 60);
        SingleInterval rest = new SingleInterval(20, 90, true, 20);
        RepeatInterval repeat = new RepeatInterval(rest, work, 3);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(repeat));

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(20, result);
    }

    @Test
    void testCalculateAverageIntensity_mixedIntervals_shouldCalculateCorrectly() {
        // Given
        // Single: 50
        // Ramp: (30 + 70) / 2 = 50
        // Repeat: work=90 (60s), rest=30 (20s), ratio=60/80=0, avg=90*0+30*1=30
        // Average of (50 + 50 + 30) / 3 = 43 (integer division)
        SingleInterval single = new SingleInterval(50, 90, true, 300);
        RampInterval ramp = new RampInterval(30, 70, 90, true, 600);
        SingleInterval work = new SingleInterval(90, 90, true, 60);
        SingleInterval rest = new SingleInterval(30, 90, true, 20);
        RepeatInterval repeat = new RepeatInterval(rest, work, 2);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(single, ramp, repeat));

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        // (50 + 50 + 30) / 3 = 43.33 -> 43 (integer division)
        assertEquals(43, result);
    }

    @Test
    void testCalculateAverageIntensity_zeroIntensity_shouldReturnZero() {
        // Given
        SingleInterval interval = new SingleInterval(0, 90, true, 300);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateAverageIntensity_rampIntervalEqualFromAndTo_shouldReturnSameValue() {
        // Given
        // Ramp from 50 to 50, average = (50 + 50) / 2 = 50
        RampInterval ramp = new RampInterval(50, 50, 90, true, 600);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(ramp));

        // When
        Integer result = workoutPowerUtils.calculateAverageIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(50, result);
    }
}
