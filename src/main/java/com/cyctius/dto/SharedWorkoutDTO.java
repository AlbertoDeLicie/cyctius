package com.cyctius.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedWorkoutDTO {
    private String name;
    private String description;
    private String intervalsJson;
}
