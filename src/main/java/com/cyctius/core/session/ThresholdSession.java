package com.cyctius.core.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThresholdSession {
    private Double workIntensity; // % of FTP (e.g., 1.05)
    private Double restIntensity; // % of FTP (e.g., 0.50)
    private Integer workDuration;  // seconds
    private Integer restDuration;  // seconds
    private Integer totalDuration; // seconds
    private Integer repeats;
    private Double score;
    private Double totalTimeInZone;       // Total Time in Zone (seconds)
}
