package com.cyctius.core.model;

import com.cyctius.core.enums.PowerZone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WorkoutTimeInZone {
    private Integer timeInZone;
    private PowerZone powerZone;

    public Double getTimeInZonePercentage(final Integer totalTime) {
        return (double) timeInZone / totalTime;
    }
    
    public WorkoutTimeInZone multiplyTime(final Integer multiplier) {
        timeInZone *= multiplier;
        return this;
    }
}
