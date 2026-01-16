package com.cyctius.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

import com.cyctius.core.model.intervals.Interval;
import com.cyctius.core.service.WorkoutMetadataCalculator;
import com.cyctius.dto.WorkoutMetadataDTO;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkoutModel {
    private String id;
    private String name;
    private String description;
    private List<Interval> intervals;
    private WorkoutMetadataDTO metadata;

    public Integer getTotalDurationSeconds() {
        return intervals.stream()
            .map(Interval::getTotalDurationSeconds)
            .reduce(0, Integer::sum);
    }

    public void recalculateMetadata(final WorkoutMetadataCalculator workoutMetadataCalculator) {
        metadata = workoutMetadataCalculator.calculateMetadata(this, true);
    }

    public boolean isValid() {
        return Objects.nonNull(intervals) && !intervals.isEmpty();
    }
}
