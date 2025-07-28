package com.cyctius.service.impl;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.entity.CyctiusUser;
import com.cyctius.service.UserTransformer;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserTransformerImpl implements UserTransformer {
    @Override
    public CyctiusUserDTO transformToDTO(final CyctiusUser user) {
        if (Objects.isNull(user)) {
            return null;
        }

        return CyctiusUserDTO.builder()
                .userId(user.getUserId())
                .issuerId(user.getIssuerId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .ftp(user.getFtp())
                .maxHR(user.getMaxHR())
                .restHR(user.getRestHR())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public CyctiusUser transformToEntity(final CyctiusUserDTO userDTO) {
        if (Objects.isNull(userDTO)) {
            return null;
        }

        return CyctiusUser.builder()
                .userId(userDTO.getUserId())
                .issuerId(userDTO.getIssuerId())
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .mobile(userDTO.getMobile())
                .ftp(userDTO.getFtp())
                .maxHR(userDTO.getMaxHR())
                .restHR(userDTO.getRestHR())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                .build();
    }
}
