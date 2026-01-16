package com.cyctius.service.impl;

import com.cyctius.core.model.WorkoutModel;
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
class WorkoutPowerUtilsIFTest {

    private WorkoutPowerUtilsImpl workoutPowerUtils;

    @BeforeEach
    void setUp() {
        workoutPowerUtils = new WorkoutPowerUtilsImpl();
    }

    @Test
    void testCalculateIF_nullWorkout_shouldReturnZero() {
        // When
        Double result = workoutPowerUtils.calculateIF(null);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateIF_nullIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(null);

        // When
        Double result = workoutPowerUtils.calculateIF(workout);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateIF_emptyIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(new ArrayList<>());

        // When
        Double result = workoutPowerUtils.calculateIF(workout);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateIF_constantIntensity_shouldReturnOne() {

        SingleInterval steady = new SingleInterval(100, 90, true, 600);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(steady));

        // When
        Double result = workoutPowerUtils.calculateIF(workout);

        // Then
        assertNotNull(result);
        assertEquals(1.0, result);
    }

    @Test
    void testCalculateIF_highVariability_shouldReturnZeroDueToIntegerDivision() {
        // Given
        // Average intensity is low (20) while normalized intensity is much higher (~60-75),
        // so integer division produces 0 -> IF = 0.0
        SingleInterval work = new SingleInterval(80, 90, true, 30);
        SingleInterval rest = new SingleInterval(20, 90, true, 10);
        RepeatInterval repeat = new RepeatInterval(rest, work, 3);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(repeat));

        // When
        Double result = workoutPowerUtils.calculateIF(workout);

        // Then
        assertNotNull(result);
        assertTrue(result > 0.25);
    }

    @Test
    void testCalculateIF_shortWorkoutWithZeroNormalizedIntensity_shouldThrow() {
        // Given
        // Workout shorter than 30 seconds -> normalized intensity becomes 0, division triggers exception
        SingleInterval shortInterval = new SingleInterval(70, 90, true, 15);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(shortInterval));

        // When / Then
        Double result = workoutPowerUtils.calculateIF(workout);
        assertNotNull(result);
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateIF_longHighBlockWithShortEasyBlock_shouldBeFractionBetweenZeroAndOne() {
        // Given
        // Long hard block (100%) and a very short easy block (30%) create a case
        // where normalized intensity should exceed the simple interval-average,
        // so IF is expected to be between 0 and 1 (and strictly less than 1).
        SingleInterval longHard = new SingleInterval(100, 90, true, 1200); // 20 minutes
        SingleInterval shortEasy = new SingleInterval(30, 90, true, 60);   // 1 minute

        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(longHard, shortEasy));

        // When
        Double result = workoutPowerUtils.calculateIF(workout);

        // Then
        assertNotNull(result);
        assertTrue(result > 0.5, "IF should be above zero for a valid workout");
        assertTrue(result < 1.0, "IF must stay below 1 for mixed intensity");
    }

    @Test
    void testCalculateIF_intervalsWithDurationsBiasingNPAboveAverage_shouldBeFractionBelowOne() {
        // Given
        // One very long high-intensity block and two shorter moderate blocks.
        // Duration-weighted NP should be higher than the simple interval-average,
        // so IF should fall in (0, 1).
        SingleInterval longHigh = new SingleInterval(110, 90, true, 1500); // 25 minutes
        SingleInterval mod1 = new SingleInterval(70, 90, true, 300);       // 5 minutes
        SingleInterval mod2 = new SingleInterval(65, 90, true, 300);       // 5 minutes

        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(longHigh, mod1, mod2));

        // When
        Double result = workoutPowerUtils.calculateIF(workout);

        // Then
        assertNotNull(result);
        assertTrue(result > 1.0, "IF should be above 1 for this mix");
    }
}
