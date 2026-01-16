package com.cyctius.core.enums;

/**
 * Training type classification based on power zones and intensity.
 * Values represent percentage ranges (0.0-1.0 scale) relative to FTP.
 */
public enum WorkoutType {
    /**
     * Z1: Active Recovery - < 55% FTP
     * Very light effort for recovery rides.
     */
    RECOVERY,

    /**
     * Z2: Endurance - 56-75% FTP
     * The aerobic "base" zone; can be maintained for hours.
     */
    ENDURANCE,

    /**
     * Z3: Tempo - 76-90% FTP
     * Moderately high effort; requires concentration.
     */
    TEMPO,

    /**
     * Z4: Threshold - 91-105% FTP
     * The maximum effort maintainable for about 60 minutes.
     */
    THRESHOLD,

    /**
     * Z5: VO2 Max - 106-120% FTP
     * High intensity, focusing on improving maximal oxygen uptake.
     */
    VO2MAX,

    /**
     * Z6: Anaerobic Capacity - 121-150% FTP
     * Short, explosive efforts (30s-2min).
     */
    ANAEROBIC,

    /**
     * Z7: Neuromuscular Power - > 150% FTP
     * All-out sprints (up to 15 seconds).
     */
    NEUROMUSCULAR
}

