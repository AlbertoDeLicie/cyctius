package com.cyctius.core.parameters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThresholdSessionParameters {
    private Double score; // 1.0 - 10.0
    private Integer durationMinutes; // Общее время сессии (Работа + Отдых)
    private ThresholdIntervalType intervalType;

    public enum ThresholdIntervalType {
        THRESHOLD,
        SWEET_SPOT
    }

    public boolean isValid() {
        return score != null && score >= 1.0 && score <= 10.0 &&
               durationMinutes != null && durationMinutes >= 10 && durationMinutes <= 120 &&
               intervalType != null;
    }
}
