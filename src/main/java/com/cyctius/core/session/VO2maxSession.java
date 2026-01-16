package com.cyctius.core.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VO2maxSession {
    private Double workIntensity; // In percentage of FTP (e.g. 1.12 for 112%)
    private Double restIntensity; // In percentage of FTP (e.g. 0.4 for 40%)
    private Integer workDuration;
    private Integer restDuration;
    private Integer totalDuration;
    private Integer repeats;
    private Double score;
}
