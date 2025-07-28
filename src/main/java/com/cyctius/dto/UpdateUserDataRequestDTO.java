package com.cyctius.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDataRequestDTO {
    private String firstName;
    private String lastName;
    private Long ftp;
    private Long maxHR;
    private Long restHR;
}
