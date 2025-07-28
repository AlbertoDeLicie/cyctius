package com.cyctius.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CyctiusUserDTO {
    private String userId;
    private String issuerId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private Long ftp;
    private Long maxHR;
    private Long restHR;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
