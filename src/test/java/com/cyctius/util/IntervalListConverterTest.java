package com.cyctius.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cyctius.core.model.intervals.Interval;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IntervalListConverterTest {

    private IntervalListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new IntervalListConverter();
    }

    @Test
    void convertToDatabaseColumn_WithNull_ReturnsEmptyJsonArray() {
        String result = converter.convertToDatabaseColumn(null);
        assertThat(result).isEqualTo("[]");
    }

    @Test
    void convertToDatabaseColumn_WithEmptyList_ReturnsEmptyJsonArray() {
        String result = converter.convertToDatabaseColumn(Collections.emptyList());
        assertThat(result).isEqualTo("[]");
    }

    @Test
    void convertToDatabaseColumn_WithIntervals_ReturnsJsonString() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(new SingleInterval(100, 90, true, 300));
        
        String result = converter.convertToDatabaseColumn(intervals);
        
        assertThat(result).contains("\"type\":\"SINGLE\"");
        assertThat(result).contains("\"targetIntensity\":100");
        assertThat(result).contains("\"duration\":300");
    }

    @Test
    void convertToEntityAttribute_WithNull_ReturnsEmptyList() {
        List<Interval> result = converter.convertToEntityAttribute(null);
        assertThat(result).isEmpty();
    }

    @Test
    void convertToEntityAttribute_WithBlank_ReturnsEmptyList() {
        List<Interval> result = converter.convertToEntityAttribute("   ");
        assertThat(result).isEmpty();
    }

    @Test
    void convertToEntityAttribute_WithInvalidJson_ReturnsEmptyList() {
        List<Interval> result = converter.convertToEntityAttribute("invalid json");
        assertThat(result).isEmpty();
    }

    @Test
    void convertToEntityAttribute_WithValidJson_ReturnsIntervalList() {
        String json = "[{\"type\":\"SINGLE\",\"targetIntensity\":100,\"targetCadence\":90,\"enableCadence\":true,\"duration\":300}]";
        
        List<Interval> result = converter.convertToEntityAttribute(json);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(SingleInterval.class);
        SingleInterval single = (SingleInterval) result.get(0);
        assertThat(single.getTargetIntensity()).isEqualTo(100);
        assertThat(single.getDuration()).isEqualTo(300);
    }

    @Test
    void roundTrip_WithMultipleIntervalTypes_PreservesData() {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(new SingleInterval(100, 90, true, 300));
        intervals.add(new RampInterval(100, 200, 90, true, 600));
        
        SingleInterval work = new SingleInterval(200, 100, true, 60);
        SingleInterval rest = new SingleInterval(100, 80, true, 30);
        intervals.add(new RepeatInterval(rest, work, 5));
        
        String dbData = converter.convertToDatabaseColumn(intervals);
        
        // Convert back to Entity
        List<Interval> result = converter.convertToEntityAttribute(dbData);
        
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isInstanceOf(SingleInterval.class);
        assertThat(result.get(1)).isInstanceOf(RampInterval.class);
        assertThat(result.get(2)).isInstanceOf(RepeatInterval.class);
        
        // Check fields manually since Interval doesn't have equals/hashCode
        assertThat(((SingleInterval)result.get(0)).getTargetIntensity()).isEqualTo(100);
        assertThat(((RampInterval)result.get(1)).getTargetIntensityFrom()).isEqualTo(100);
        assertThat(((RepeatInterval)result.get(2)).getRepeats()).isEqualTo(5);
    }
}
