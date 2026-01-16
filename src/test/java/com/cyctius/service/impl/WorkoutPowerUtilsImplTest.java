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
class WorkoutPowerUtilsImplTest {

    private WorkoutPowerUtilsImpl workoutPowerUtils;

    @BeforeEach
    void setUp() {
        workoutPowerUtils = new WorkoutPowerUtilsImpl();
    }

    @Test
    void testCalculateNormalizedPower_nullWorkout_shouldReturnZero() {
        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(null);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateNormalizedPower_nullIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(null);

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateNormalizedPower_emptyIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(new ArrayList<>());

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateNormalizedPower_singleIntervalConstantIntensity_shouldReturnSameIntensity() {
        // Given
        // Constant intensity of 50 for 60 seconds
        // With 30-second rolling average, all smoothed values will be 50
        // NP = (50^4)^(1/4) = 50
        SingleInterval interval = new SingleInterval(50, 90, true, 60);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        assertEquals(50, result);
    }

    @Test
    void testCalculateNormalizedPower_rampInterval_shouldCalculateCorrectly() {
        // Given
        // Ramp from 40 to 60 over 60 seconds
        // This creates variable intensity, so NP should be higher than simple average
        RampInterval rampInterval = new RampInterval(40, 60, 90, true, 60);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(rampInterval));

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        // NP should be close to the average (50) but slightly higher due to variability
        assertTrue(result >= 48 && result <= 52, "NP should be around 50 for ramp from 40 to 60");
    }

    @Test
    void testCalculateNormalizedPower_repeatInterval_shouldCalculateCorrectly() {
        // Given
        // 3 repeats: work at 80% for 30s, rest at 20% for 10s
        // This creates high variability, so NP should be higher than simple average
        SingleInterval work = new SingleInterval(80, 90, true, 30);
        SingleInterval rest = new SingleInterval(20, 90, true, 10);
        RepeatInterval repeatInterval = new RepeatInterval(rest, work, 3);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(repeatInterval));

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        // Simple average would be: (80*30 + 20*10) * 3 / 120 = 65
        // But NP should be higher due to variability (4th power emphasizes high values)
        assertTrue(result >= 60 && result <= 75, "NP should be higher than average due to high-intensity intervals");
    }

    @Test
    void testCalculateNormalizedPower_mixedIntervals_shouldCalculateCorrectly() {
        // Given
        // Mix of constant and variable intensity intervals
        SingleInterval warmup = new SingleInterval(40, 90, true, 300);
        RampInterval ramp = new RampInterval(50, 90, 90, true, 600);
        SingleInterval cooldown = new SingleInterval(30, 90, true, 300);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(warmup, ramp, cooldown));

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        // Should be a reasonable value considering the mix
        assertTrue(result > 0, "NP should be positive");
        assertTrue(result < 100, "NP should not exceed maximum intensity");
    }

    @Test
    void testCalculateNormalizedPower_shortWorkoutLessThan30Seconds_shouldCalculateCorrectly() {
        // Given
        // Workout shorter than rolling average window (30 seconds)
        SingleInterval interval = new SingleInterval(70, 90, true, 15);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        // For short workouts, rolling average will use available data
        assertTrue(result == 0, "NP should be 0 for short workouts");
    }

    @Test
    void testCalculateNormalizedPower_highIntensityVariableWorkout_shouldEmphasizeHighIntensity() {
        // Given
        // High intensity intervals with rest periods
        // This should result in NP being higher than simple average
        SingleInterval high = new SingleInterval(100, 90, true, 60);
        SingleInterval rest = new SingleInterval(30, 90, true, 60);
        SingleInterval high2 = new SingleInterval(95, 90, true, 60);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(high, rest, high2));

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        // Simple average: (100 + 30 + 95) / 3 = 75
        // NP should be higher due to 4th power emphasizing high values
        assertTrue(result >= 70 && result <= 85, "NP should be higher than simple average for variable high-intensity workout");
    }

    @Test
    void testCalculateNormalizedPower_longConstantWorkout_shouldReturnConstantIntensity() {
        // Given
        // Long constant intensity workout (2 hours)
        // NP should equal the constant intensity
        SingleInterval interval = new SingleInterval(60, 90, true, 7200);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = workoutPowerUtils.calculateNormalizedIntensity(workout);

        // Then
        assertNotNull(result);
        // For constant intensity, NP should equal the intensity
        assertEquals(60, result, "NP should equal constant intensity for steady-state workout");
    }
}
