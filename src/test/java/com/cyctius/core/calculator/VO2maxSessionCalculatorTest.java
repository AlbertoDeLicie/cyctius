package com.cyctius.core.calculator;

import com.cyctius.core.parameters.VO2maxSessionParameters;
import com.cyctius.core.profile.AthleteVO2MaxProfile;
import com.cyctius.core.session.VO2maxSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class VO2maxSessionCalculatorTest {

    private VO2maxSessionCalculator calculator;
    private AthleteVO2MaxProfile defaultAthlete;

    @BeforeEach
    void setUp() {
        calculator = new VO2maxSessionCalculator();
        // FTP 266W, 120% TTE = 5 min, 106% TTE = 21 min (W' ~ 20.1kJ)
        defaultAthlete = AthleteVO2MaxProfile.builder()
                .ftp(266)
                .tte120Min(5.0)
                .tte106Min(21.0)
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
    @DisplayName("Should return null when athlete is null (or handle gracefully)")
    void calculate_WithNullAthlete_ThrowsNullPointerException() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(8.0)
                .durationMinutes(30)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.CLASSIC)
                .build();
        
        assertThrows(NullPointerException.class, () -> 
            calculator.calculate(null, params)
        );
    }

    @Test
    @DisplayName("Should throw exception when parameters are invalid (score > 10)")
    void calculate_WithInvalidScore_ThrowsException() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(11.0)
                .durationMinutes(30)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.CLASSIC)
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.calculate(defaultAthlete, params)
        );
    }

    @Test
    @DisplayName("Classic Intervals: Normal Data (Score 8.0, 30 min)")
    void calculate_ClassicIntervals_NormalData_Success() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(8.0)
                .durationMinutes(30)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.CLASSIC)
                .build();

        VO2maxSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(1800, session.getTotalDuration()); // 30 min * 60
        assertTrue(session.getWorkIntensity() > 1.0); // Now in % of FTP
        assertTrue(session.getRepeats() >= 2);
        assertEquals(8.0, session.getScore());
    }

    @Test
    @DisplayName("Short Intervals: Normal Data (Score 7.0, 20 min)")
    void calculate_ShortIntervals_NormalData_Success() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(7.0)
                .durationMinutes(20)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.SHORT)
                .build();

        VO2maxSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(1200, session.getTotalDuration());
        assertEquals(7.0, session.getScore());
        // Short intervals work duration should be within range [30, 60]
        assertTrue(session.getWorkDuration() >= 30 && session.getWorkDuration() <= 60);
    }

    @Test
    @DisplayName("Long Intervals: Normal Data (Score 6.0, 40 min)")
    void calculate_LongIntervals_NormalData_Success() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(6.0)
                .durationMinutes(40)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.LONG)
                .build();

        VO2maxSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(2400, session.getTotalDuration());
        assertEquals(6.0, session.getScore());
        // Long intervals work duration should be within range [300, 600]
        assertTrue(session.getWorkDuration() >= 300);
    }

    @Test
    @DisplayName("Maximum Difficulty: Score 10.0")
    void calculate_MaxDifficulty_Score10_Success() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(10.0)
                .durationMinutes(30)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.CLASSIC)
                .build();

        VO2maxSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(10.0, session.getScore());
        // High intensity or many repeats expected for score 10
    }

    @Test
    @DisplayName("Minimum Difficulty: Score 1.0")
    void calculate_MinDifficulty_Score1_Success() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(1.0)
                .durationMinutes(25)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.CLASSIC)
                .build();

        VO2maxSession session = calculator.calculate(defaultAthlete, params);

        assertNotNull(session);
        assertEquals(1.0, session.getScore());
    }

    @Test
    @DisplayName("Very short interval but with long interval type")
    void calculate_VeryShortInterval_LongIntervalType_Success() {
        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(8.0)
                .durationMinutes(10)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.LONG)
                .build();

        VO2maxSession session = calculator.calculate(defaultAthlete, params);

        assertNull(session);
    }

    @Test
    @DisplayName("High FTP Athlete (400W)")
    void calculate_HighFtpAthlete_Success() {
        AthleteVO2MaxProfile proAthlete = AthleteVO2MaxProfile.builder()
                .ftp(400)
                .tte120Min(4.0)
                .tte106Min(15.0)
                .build();

        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(8.5)
                .durationMinutes(35)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.CLASSIC)
                .build();

        VO2maxSession session = calculator.calculate(proAthlete, params);

        assertNotNull(session);
        assertTrue(session.getWorkIntensity() > 1.0); // Now in % of FTP
    }

    @Test
    @DisplayName("Low Anaerobic Capacity Athlete")
    void calculate_LowWPrimeAthlete_Success() {
        AthleteVO2MaxProfile dieselAthlete = AthleteVO2MaxProfile.builder()
                .ftp(200)
                .tte120Min(1.0) // Very low TTE at high intensity
                .tte106Min(5.0)
                .build();

        VO2maxSessionParameters params = VO2maxSessionParameters.builder()
                .score(5.0)
                .durationMinutes(20)
                .intervalType(VO2maxSessionParameters.VO2maxIntervalType.SHORT)
                .build();

        VO2maxSession session = calculator.calculate(dieselAthlete, params);

        assertNotNull(session);
        // Should still find a session but might have many short intervals or low power
    }
}
