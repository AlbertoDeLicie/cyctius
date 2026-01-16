package com.cyctius.util;

import com.cyctius.core.model.WorkoutModel;
import com.cyctius.core.model.intervals.Interval;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.core.enums.IntervalType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for parsing intervals JSON strings into WorkoutModel objects.
 */
@Component
public class IntervalJsonParser {

    private final ObjectMapper objectMapper;

    public IntervalJsonParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Parse intervals JSON string into WorkoutModel.
     *
     * @param intervalsJson the JSON string containing intervals
     * @return WorkoutModel with parsed intervals
     * @throws RuntimeException if parsing fails
     */
    public Optional<WorkoutModel> parseIntervalsJson(String intervalsJson) {
        if (intervalsJson == null || intervalsJson.isBlank()) {
            return Optional.empty();
        }

        try {
            JsonNode rootNode = objectMapper.readTree(intervalsJson);
            JsonNode intervalsNode = rootNode.has("intervals") ? rootNode.get("intervals") : rootNode;

            List<Interval> intervals = new ArrayList<>();
            
            if (intervalsNode.isArray()) {
                for (JsonNode intervalNode : intervalsNode) {
                    Interval interval = parseInterval(intervalNode);
                    if (interval != null) {
                        intervals.add(interval);
                    }
                }
            }

            return Optional.ofNullable(
                WorkoutModel.builder()
                    .intervals(intervals)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse intervals JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Parse a single interval from JSON node.
     */
    private Interval parseInterval(JsonNode intervalNode) {
        if (!intervalNode.has("type")) {
            return null;
        }

        String typeStr = intervalNode.get("type").asText();
        IntervalType type = IntervalType.valueOf(typeStr);

        try {
            return switch (type) {
                case SINGLE -> objectMapper.treeToValue(intervalNode, SingleInterval.class);
                case REPEAT -> objectMapper.treeToValue(intervalNode, RepeatInterval.class);
                case RAMP -> objectMapper.treeToValue(intervalNode, RampInterval.class);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse interval of type " + type + ": " + e.getMessage(), e);
        }
    }

    /**
     * Convert WorkoutModel back to JSON string.
     *
     * @param workoutModel the workout model to serialize
     * @return JSON string representation
     */
    public String toJsonString(WorkoutModel workoutModel) {
        if (workoutModel == null) {
            return "{\"intervals\":[]}";
        }

        try {
            return objectMapper.writeValueAsString(workoutModel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize workout model to JSON: " + e.getMessage(), e);
        }
    }
}

