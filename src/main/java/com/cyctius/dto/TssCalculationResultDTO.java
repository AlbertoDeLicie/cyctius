package com.cyctius.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TssCalculationResultDTO {
    private Integer tss;
    private Integer totalDurationSeconds;
}

