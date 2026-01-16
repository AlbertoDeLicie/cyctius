package com.cyctius.core.parameters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VO2maxSessionParameters {
    private Double score; // 1.0 - 10.0
    private Integer durationMinutes;
    private VO2maxIntervalType intervalType;

    public enum VO2maxIntervalType {
        SHORT,       // Короткие (e.g. 40/20)
        CLASSIC,     // Классические (e.g. 3-4 min)
        LONG         // Длинные (e.g. 5-8 min)
    }

    public boolean isValid() {
        return score != null && score >= 1.0 && score <= 10.0 &&
               durationMinutes != null && durationMinutes >= 10 && durationMinutes <= 60 &&
               intervalType != null;
    }
}
