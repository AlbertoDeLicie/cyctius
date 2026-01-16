package com.cyctius.core.model.intervals;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;

import com.cyctius.core.enums.IntervalType;

@Data
@EqualsAndHashCode(callSuper = true)
public class RepeatInterval extends Interval {
    private SingleInterval rest;
    private SingleInterval work;
    private Integer repeats;

    public RepeatInterval() {
        super(IntervalType.REPEAT);
    }

    public RepeatInterval(SingleInterval rest, SingleInterval work, Integer repeats) {
        super(IntervalType.REPEAT);
        this.rest = rest;
        this.work = work;
        this.repeats = repeats;
    }

    @Override
    public Integer getTotalDurationSeconds() {
        return (work.getDuration() + rest.getDuration()) * repeats;
    }

    @Override
    public Integer averageIntensity() {
        val ratio = work.getDuration() / (work.getDuration() + rest.getDuration());
        return (work.getTargetIntensity() * ratio + rest.getTargetIntensity() * (1 - ratio));
    }
}
