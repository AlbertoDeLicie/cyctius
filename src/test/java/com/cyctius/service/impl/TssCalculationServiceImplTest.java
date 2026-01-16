package com.cyctius.service.impl;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.core.service.impl.TssCalculationServiceImpl;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TssCalculationServiceImplTest {

    private TssCalculationServiceImpl tssCalculationService;

    @BeforeEach
    void setUp() {
        tssCalculationService = new TssCalculationServiceImpl();
    }

    // ==================== calculateTssForSession Tests ====================

    @Test
    void testCalculateTssForSession_nullWorkout_shouldReturnZero() {
        // When
        Integer result = tssCalculationService.calculateTssForSession(null);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForSession_nullIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(null);

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForSession_emptyIntervals_shouldReturnZero() {
        // Given
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(new ArrayList<>());

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForSession_singleInterval_shouldCalculateCorrectly() {
        // Given
        // Single interval: intensity=50, duration=3600 seconds (1 hour)
        // Expected TSS: (3600 * 50 * 50) / 3600 / 100 = 25
        SingleInterval interval = new SingleInterval(50, 90, true, 3600);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(25, result);
    }

    @Test
    void testCalculateTssForSession_multipleSingleIntervals_shouldSumCorrectly() {

        SingleInterval interval1 = new SingleInterval(60, 90, true, 1800);
        SingleInterval interval2 = new SingleInterval(40, 90, true, 1800);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval1, interval2));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(26, result);
    }

    @Test
    void testCalculateTssForSession_withRepeatInterval_shouldCalculateCorrectly() {
        SingleInterval work = new SingleInterval(70, 90, true, 300);
        SingleInterval rest = new SingleInterval(30, 90, true, 60);
        RepeatInterval repeatInterval = new RepeatInterval(rest, work, 3);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(repeatInterval));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(12, result); // Allow small rounding error
    }

    @Test
    void testCalculateTssForSession_withRampInterval_shouldCalculateCorrectly() {
        RampInterval rampInterval = new RampInterval(40, 60, 90, true, 1800);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(rampInterval));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(13, result);
    }

    @Test
    void testCalculateTssForSession_mixedIntervals_shouldCalculateCorrectly() {
        // Given
        SingleInterval single1 = new SingleInterval(50, 90, true, 600); // TSS: 41666.67
        RampInterval ramp = new RampInterval(30, 70, 90, true, 1200); // Average: 50, TSS: 83333.33
        SingleInterval single2 = new SingleInterval(40, 90, true, 300); // TSS: 13333.33
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(single1, ramp, single2));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(13, result); // Allow small rounding error
    }

    // ==================== calculateTssForInterval Tests ====================

    @Test
    void testCalculateTssForInterval_nullInterval_shouldReturnZero() {
        // When
        val result = tssCalculationService.calculateTssForInterval(null);

        // Then
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForInterval_singleInterval_shouldCalculateCorrectly() {
        // Given
        // intensity=80, duration=900
        // Expected TSS: (900 * 80 * 80) / 3600 / 100 = 16
        SingleInterval interval = new SingleInterval(80, 90, true, 900);

        // When
        val result = tssCalculationService.calculateTssForInterval(interval);

        // Then
        assertEquals(16, result);
    }

    @Test
    void testCalculateTssForInterval_singleInterval_zeroDuration_shouldReturnZero() {
        // Given
        SingleInterval interval = new SingleInterval(50, 90, true, 0);

        // When
        val result = tssCalculationService.calculateTssForInterval(interval);

        // Then
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForInterval_singleInterval_zeroIntensity_shouldReturnZero() {
        // Given
        SingleInterval interval = new SingleInterval(0, 90, true, 3600);

        // When
        val result = tssCalculationService.calculateTssForInterval(interval);

        // Then
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForInterval_repeatInterval_shouldCalculateCorrectly() {
        // Given
        // work: intensity=75, duration=400 -> TSS: 6.25
        // rest: intensity=25, duration=100 -> TSS: 0
        // repeats=2
        // Expected: (6 + 0) * 2 = 14
        SingleInterval work = new SingleInterval(75, 90, true, 400);
        SingleInterval rest = new SingleInterval(25, 90, true, 100);
        RepeatInterval repeatInterval = new RepeatInterval(rest, work, 2);

        // When
        val result = tssCalculationService.calculateTssForInterval(repeatInterval);

        // Then
        assertEquals(12, result); // Allow small rounding error
    }

    @Test
    void testCalculateTssForInterval_repeatInterval_zeroRepeats_shouldReturnZero() {
        // Given
        SingleInterval work = new SingleInterval(70, 90, true, 300);
        SingleInterval rest = new SingleInterval(30, 90, true, 60);
        RepeatInterval repeatInterval = new RepeatInterval(rest, work, 0);

        // When
        val result = tssCalculationService.calculateTssForInterval(repeatInterval);

        // Then
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForInterval_rampInterval_shouldCalculateCorrectly() {
        RampInterval rampInterval = new RampInterval(20, 80, 90, true, 1800);

        // When
        val result = tssCalculationService.calculateTssForInterval(rampInterval);

        // Then
        assertEquals(13, result);
    }

    @Test
    void testCalculateTssForInterval_rampInterval_equalFromAndTo_shouldCalculateCorrectly() {
        RampInterval rampInterval = new RampInterval(50, 50, 90, true, 3600);

        // When
        val result = tssCalculationService.calculateTssForInterval(rampInterval);

        // Then
        assertEquals(25, result);
    }

    @Test
    void testCalculateTssForInterval_rampInterval_zeroDuration_shouldReturnZero() {
        // Given
        RampInterval rampInterval = new RampInterval(40, 60, 90, true, 0);

        // When
        val result = tssCalculationService.calculateTssForInterval(rampInterval);

        // Then
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForInterval_rampInterval_zeroIntensities_shouldReturnZero() {
        // Given
        RampInterval rampInterval = new RampInterval(0, 0, 90, true, 3600);

        // When
        val result = tssCalculationService.calculateTssForInterval(rampInterval);

        // Then
        assertEquals(0, result);
    }

    // ==================== Edge Cases ====================

    @Test
    void testCalculateTssForSession_smallValues_shouldCalculateCorrectly() {
        SingleInterval interval = new SingleInterval(10, 90, true, 60);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    void testCalculateTssForSession_highIntensity_shouldCalculateCorrectly() {
        // Given
        // High intensity interval
        // intensity=100, duration=600
        // Expected TSS: (600 * 100 * 100) / 3600 * 100 = 1666666.67
        SingleInterval interval = new SingleInterval(100, 90, true, 600);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(17, result);
    }

    @Test
    void testCalculateTssForSession_veryLongDuration_shouldCalculateCorrectly() {
        // Given
        // Very long interval
        // intensity=50, duration=7200 (2 hours)
        // Expected TSS: (7200 * 50 * 50) / 3600 * 100 = 500000
        SingleInterval interval = new SingleInterval(50, 90, true, 7200);
        WorkoutModel workout = new WorkoutModel();
        workout.setIntervals(List.of(interval));

        // When
        Integer result = tssCalculationService.calculateTssForSession(workout);

        // Then
        assertNotNull(result);
        assertEquals(50, result);
    }

    @Test
    void testCalculateTssForInterval_repeatInterval_withZeroRestIntensity_shouldCalculateCorrectly() {
        // Given
        SingleInterval work = new SingleInterval(70, 90, true, 300); // TSS: 40833.33
        SingleInterval rest = new SingleInterval(0, 90, true, 60); // TSS: 0
        RepeatInterval repeatInterval = new RepeatInterval(rest, work, 2);

        // When
        val result = tssCalculationService.calculateTssForInterval(repeatInterval);

        // Then
        assertEquals(8, result); // Allow small rounding error
    }

    @Test
    void testCalculateTssForInterval_repeatInterval_singleRepeat_shouldCalculateCorrectly() {
        // Given
        SingleInterval work = new SingleInterval(60, 90, true, 200); // TSS: 20000
        SingleInterval rest = new SingleInterval(20, 90, true, 100); // TSS: 1111.11
        RepeatInterval repeatInterval = new RepeatInterval(rest, work, 1);

        // When
        val result = tssCalculationService.calculateTssForInterval(repeatInterval);

        // Then
        assertEquals(2, result); // Allow small rounding error
    }
}

