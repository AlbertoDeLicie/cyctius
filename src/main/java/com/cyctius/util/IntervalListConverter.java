package com.cyctius.util;

import com.cyctius.core.model.intervals.Interval;
import com.cyctius.core.model.intervals.RampInterval;
import com.cyctius.core.model.intervals.RepeatInterval;
import com.cyctius.core.model.intervals.SingleInterval;
import com.cyctius.core.enums.IntervalType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
@Converter
public class IntervalListConverter implements AttributeConverter<List<Interval>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Interval> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            String json = objectMapper.writeValueAsString(attribute);
            log.info("Converted List<Interval> to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting List<Interval> to JSON string", e);
            return "[]";
        }
    }

    @Override
    public List<Interval> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }

        try {
            JsonNode rootNode = objectMapper.readTree(dbData);
            List<Interval> intervals = new java.util.ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    Interval interval = parseInterval(node);
                    if (interval != null) {
                        intervals.add(interval);
                    }
                }
            }
            return intervals;
        } catch (Exception e) {
            log.error("Error converting JSON string to List<Interval>. JSON: {}", dbData, e);
            return Collections.emptyList();
        }
    }

    private Interval parseInterval(JsonNode node) throws JsonProcessingException {
        if (!node.has("type")) {
            return null;
        }

        String typeStr = node.get("type").asText();
        try {
            IntervalType type = IntervalType.valueOf(typeStr);
            return switch (type) {
                case SINGLE -> objectMapper.treeToValue(node, SingleInterval.class);
                case REPEAT -> objectMapper.treeToValue(node, RepeatInterval.class);
                case RAMP -> objectMapper.treeToValue(node, RampInterval.class);
            };
        } catch (IllegalArgumentException e) {
            log.error("Unknown interval type: {}", typeStr);
            return null;
        }
    }
}
