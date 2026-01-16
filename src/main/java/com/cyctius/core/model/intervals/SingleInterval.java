package com.cyctius.core.model.intervals;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.cyctius.core.enums.IntervalType;

@Data
@EqualsAndHashCode(callSuper = true)
public class SingleInterval extends Interval {
    private Integer targetIntensity;
    private Integer targetCadence;
    private Boolean enableCadence;
    private Integer duration;

    public SingleInterval() {
        super(IntervalType.SINGLE);
    }

    public SingleInterval(
            Integer targetIntensity,
            Integer targetCadence,
            Boolean enableCadence,
            Integer duration
    ) {
        super(IntervalType.SINGLE);
        this.targetIntensity = targetIntensity;
        this.targetCadence = targetCadence;
        this.enableCadence = enableCadence;
        this.duration = duration;
    }

    @Override
    public Integer getTotalDurationSeconds() {
        return duration;
    }

    @Override
    public Integer averageIntensity() {
        return targetIntensity;
    }
}
