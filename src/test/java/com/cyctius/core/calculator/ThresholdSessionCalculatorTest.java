package com.cyctius.core.calculator;

import com.cyctius.core.parameters.ThresholdSessionParameters;
import com.cyctius.core.profile.ThresholdAthleteProfile;
import com.cyctius.core.session.ThresholdSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/*
 *          [85.0, 4500.0],
            [86.0, 3420.0],
            [87.0, 3180.0],
            [88.0, 2940.0],
            [89.0, 2880.0],
            [90.0, 2640.0],
            [91.0, 2520.0],
            [92.0, 2400.0],
            [93.0, 1980.0],
            [94.0, 1820.0],
            [95.0, 1800.0],
            [96.0, 1740.0],
            [97.0, 1560.0],
            [98.0, 1500.0],
            [99.0, 1440.0],
            [100.0, 1380.0],
            [101.0, 1320.0],
            [102.0, 1260.0],
            [103.0, 1200.0],
            [104.0, 1140.0],
            [105.0, 960.0],
 */

class ThresholdSessionCalculatorTest {

    private ThresholdSessionCalculator calculator;
    private ThresholdAthleteProfile defaultAthlete;

    @BeforeEach
    void setUp() {
        calculator = new ThresholdSessionCalculator();
        // FTP 250W, PD: 85%@90min (5400s), 100%@40min (2400s), 105%@20min (1200s)
        defaultAthlete = ThresholdAthleteProfile.builder()
                .ftp(266)
                .tteAtFtp(2400.0) // 40 min
                .pdPoints(Arrays.asList(
                        new ThresholdAthleteProfile.PdPoint(85.0, 4500.0),
                        new ThresholdAthleteProfile.PdPoint(86.0, 3420.0),
                        new ThresholdAthleteProfile.PdPoint(87.0, 3180.0),
                        new ThresholdAthleteProfile.PdPoint(88.0, 2940.0),
                        new ThresholdAthleteProfile.PdPoint(89.0, 2880.0),
                        new ThresholdAthleteProfile.PdPoint(90.0, 2640.0),
                        new ThresholdAthleteProfile.PdPoint(91.0, 2520.0),
                        new ThresholdAthleteProfile.PdPoint(92.0, 2400.0),
                        new ThresholdAthleteProfile.PdPoint(93.0, 1980.0),
                        new ThresholdAthleteProfile.PdPoint(94.0, 1820.0),
                        new ThresholdAthleteProfile.PdPoint(95.0, 1800.0),
                        new ThresholdAthleteProfile.PdPoint(96.0, 1740.0),
                        new ThresholdAthleteProfile.PdPoint(97.0, 1560.0),
                        new ThresholdAthleteProfile.PdPoint(98.0, 1500.0),
                        new ThresholdAthleteProfile.PdPoint(99.0, 1440.0),
                        new ThresholdAthleteProfile.PdPoint(100.0, 1380.0),
                        new ThresholdAthleteProfile.PdPoint(101.0, 1320.0),
                        new ThresholdAthleteProfile.PdPoint(102.0, 1260.0),
                        new ThresholdAthleteProfile.PdPoint(103.0, 1200.0),
                        new ThresholdAthleteProfile.PdPoint(104.0, 1140.0),
                        new ThresholdAthleteProfile.PdPoint(105.0, 960.0)
                ))
                .build();
    }

    @Test
    @DisplayName("Should throw exception when parameters are null")
    void calculate_WithNullParameters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculate(defaultAthlete, null)
        );
    }

    @Test
    @DisplayName("Threshold Session: Score 8.9")
    void calculate_Threshold_Score8_9_Success() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(8.9)
                .durationMinutes(33)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.THRESHOLD)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(33 * 60, session.getTotalDuration());
        assertEquals(8.9, session.getScore());
        assertEquals(2, session.getRepeats());

        assertTrue(session.getWorkIntensity() > 0.95, "Intensity should be at least 100% FTP");
    }

    @Test
    @DisplayName("Threshold Session: Score 10.0 (All-out)")
    void calculate_Threshold_Score10_Success() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(10.0)
                .durationMinutes(40)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.THRESHOLD)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(2400, session.getTotalDuration());
        assertEquals(10.0, session.getScore());
        assertEquals(2, session.getRepeats()); // Score 10 should have 2 repeats
        
        assertTrue(session.getWorkIntensity() >= 0.95, "Intensity should be at least 100% FTP");
    }

    @Test
    @DisplayName("Threshold Session: Score 10.0 but Short Duration (All-out)")
    void calculate_Threshold_Score10_ShortDuration_Success() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(10.0)
                .durationMinutes(20)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.THRESHOLD)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(20 * 60, session.getTotalDuration());
        assertEquals(10.0, session.getScore());
        assertEquals(2, session.getRepeats()); // Score 10 should have 2 repeats
        
        assertTrue(session.getWorkIntensity() > 1.0, "Intensity should be at least 100% FTP");
    }

    
    @Test
    @DisplayName("Threshold Session: Score 10.0 but Short Duration 25 minutes (All-out)")
    void calculate_Threshold_Score10_ShortDuration_25_Minutes_Success() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(10.0)
                .durationMinutes(25)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.THRESHOLD)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(25 * 60, session.getTotalDuration());
        assertEquals(10.0, session.getScore());
        assertEquals(2, session.getRepeats()); // Score 10 should have 2 repeats
        
        assertTrue(session.getWorkIntensity() > 1.0, "Intensity should be at least 100% FTP");
    }

    @Test
    @DisplayName("Threshold Session: Score 6.0 but Short Duration 25 minutes")
    void calculate_Threshold_Score6_ShortDuration_25_Minutes_Success() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(6.0)
                .durationMinutes(25)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.THRESHOLD)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(25 * 60, session.getTotalDuration());
        assertEquals(6.0, session.getScore());
        assertEquals(3, session.getRepeats()); // Score 10 should have 2 repeats
        
        assertTrue(session.getWorkIntensity() >= 0.95, "Intensity should be at least 85% FTP");
    }

    @Test
    @DisplayName("Sweet Spot Session: Score 5.0")
    void calculate_SweetSpot_Score5_Success() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(5.0)
                .durationMinutes(60)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.SWEET_SPOT)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(3600, session.getTotalDuration());
        assertEquals(5.0, session.getScore());
        // Sweet spot cap ~95%
        assertTrue(session.getWorkIntensity() <= 0.95, "Sweet spot intensity should be capped");
    }

    @Test
    @DisplayName("Short Session: High Intensity Compensation")
    void calculate_ShortSession_HighIntensity() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(9.0)
                .durationMinutes(20)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.THRESHOLD)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        // Для 20 мин общего времени, интенсивность должна быть высокой, 
        // так как pMax для короткого времени выше.
        assertTrue(session.getWorkIntensity() > 1.03, "Short session should have higher intensity");
    }

    @Test
    @DisplayName("Low Score: Many Repeats and Long Rest")
    void calculate_LowScore_ManyRepeats() {
        ThresholdSessionParameters params = ThresholdSessionParameters.builder()
                .score(2.0)
                .durationMinutes(60)
                .intervalType(ThresholdSessionParameters.ThresholdIntervalType.THRESHOLD)
                .build();

        ThresholdSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertTrue(session.getRepeats() >= 6, "Low score should have more repeats");
        // restRatio на Score 2.0 ~ 0.9. Отдых должен быть сопоставим с работой.
        assertTrue(session.getRestDuration() > session.getWorkDuration() * 0.8);
    }
}
