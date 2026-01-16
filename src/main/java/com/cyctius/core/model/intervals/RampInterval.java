package com.cyctius.core.model.intervals;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.cyctius.core.enums.IntervalType;

@Data
@EqualsAndHashCode(callSuper = true)
public class RampInterval extends Interval {
    private Integer targetIntensityFrom;
    private Integer targetIntensityTo;
    private Integer targetCadence;
    private Boolean enableCadence;
    private Integer duration;

    public RampInterval() {
        super(IntervalType.RAMP);
        this.targetIntensityFrom = 10;
        this.targetIntensityTo = 50;
        this.targetCadence = 90;
        this.enableCadence = true;
        this.duration = 300;
    }

    public RampInterval(
        Integer targetIntensityFrom,
        Integer targetIntensityTo,
        Integer targetCadence,
        Boolean enableCadence,
        Integer duration
    ) {
        super(IntervalType.RAMP);
        this.targetIntensityFrom = targetIntensityFrom;
        this.targetIntensityTo = targetIntensityTo;
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
        return (targetIntensityFrom + targetIntensityTo) / 2;
    }
}
