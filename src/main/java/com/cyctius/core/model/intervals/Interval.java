package com.cyctius.core.model.intervals;

import com.cyctius.core.enums.IntervalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleInterval.class, name = "SINGLE"),
        @JsonSubTypes.Type(value = RepeatInterval.class, name = "REPEAT"),
        @JsonSubTypes.Type(value = RampInterval.class, name = "RAMP")
})
@NoArgsConstructor
@AllArgsConstructor
public abstract class Interval {
    private IntervalType type;

    @JsonIgnore
    public Integer getTotalDurationSeconds() {
        return 0;
    }

    public Integer averageIntensity() {
        return 0;
    }
}
