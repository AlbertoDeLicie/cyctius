package com.cyctius.service.impl;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.repository.UserRepository;
import com.cyctius.service.InternalUserService;
import com.cyctius.service.UserTransformer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InternalUserServiceImpl implements InternalUserService {

    private final UserRepository userRepository;
    private final UserTransformer userTransformer;

    @Override
    public CyctiusUserDTO updateUserData(final CyctiusUserDTO updatedUserDTO) {
        if (Objects.isNull((updatedUserDTO))) {
            throw new IllegalArgumentException("User data cannot be null");
        }

        validateUserData(updatedUserDTO);

        userRepository.findByIssuerId(updatedUserDTO.getIssuerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with issuer ID: " + updatedUserDTO.getIssuerId()));

        // Save the updated user
        val updatedUser = userRepository.save(userTransformer.transformToEntity(updatedUserDTO));

        return userTransformer.transformToDTO(updatedUser);
    }

    @Override
    public CyctiusUserDTO getCurrentUser() {
        val auth = SecurityContextHolder.getContext().getAuthentication();
        val issuerId = auth.getName();

        if (StringUtils.isBlank(issuerId)) {
            throw new IllegalArgumentException("Issuer ID cannot be null or empty");
        }

        val user = userRepository.findByIssuerId(issuerId).orElseThrow(
                () -> new IllegalArgumentException("User not found with issuer ID: " + issuerId)
        );

        return userTransformer.transformToDTO(user);
    }

    @Override
    public CyctiusUserDTO findByUsername(final String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        val user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("User not found with username: " + username)
        );

        return userTransformer.transformToDTO(user);
    }

    @Override
    public Boolean existsByUsername(final String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return userRepository.existsByEmail(email);
    }

    @Override
    public CyctiusUserDTO findByEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        val user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("User not found with email: " + email)
        );

        return userTransformer.transformToDTO(user);
    }

    @Override
    public CyctiusUserDTO findByIssuerId(final String issuerId) {
        if (StringUtils.isBlank(issuerId)) {
            throw new IllegalArgumentException("Issuer ID cannot be null or empty");
        }

        val user = userRepository.findByIssuerId(issuerId).orElseThrow(
                () -> new IllegalArgumentException("User not found with issuer ID: " + issuerId)
        );

        return userTransformer.transformToDTO(user);
    }

    private void validateUserData(final CyctiusUserDTO userDTO) {
        if (StringUtils.isBlank(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (StringUtils.isBlank(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (StringUtils.isBlank(userDTO.getIssuerId())) {
            throw new IllegalArgumentException("Issuer ID cannot be null or empty");
        }
        if (StringUtils.isBlank(userDTO.getFirstName())) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (StringUtils.isBlank(userDTO.getLastName())) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (userDTO.getFtp() == null || userDTO.getFtp() < 10) {
            throw new IllegalArgumentException("FTP must be a non-negative number");
        }
        if (userDTO.getMaxHR() == null || userDTO.getMaxHR() < 30) {
            throw new IllegalArgumentException("Max HR must be a non-negative number");
        }
        if (userDTO.getRestHR() == null || userDTO.getRestHR() < 30) {
            throw new IllegalArgumentException("Rest HR must be a non-negative number");
        }
        if (userDTO.getRestHR() > userDTO.getMaxHR()) {
            throw new IllegalArgumentException("Rest HR cannot be greater than Max HR");
        }
    }
}
