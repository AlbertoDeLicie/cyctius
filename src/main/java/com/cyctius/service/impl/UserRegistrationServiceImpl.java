package com.cyctius.service.impl;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.dto.UserRegistrationRequestDTO;
import com.cyctius.entity.CyctiusUser;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.repository.UserRepository;
import com.cyctius.service.InternalUserService;
import com.cyctius.service.UserRegistrationService;
import com.cyctius.service.UserTransformer;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final UserTransformer userTransformer;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public CyctiusUserDTO registerUser(final UserRegistrationRequestDTO requestDTO) {
        if (Objects.isNull(requestDTO)) {
            throw new BadRequestException("error.user.registration.null");
        }

        // register user in Keycloak. fall if response is bad
        val issuerId = registerUserInKeycloak(requestDTO);

        return registerUserInternal(requestDTO, issuerId);
    }

    private String registerUserInKeycloak(final UserRegistrationRequestDTO requestDTO) {
        val user = new UserRepresentation();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(false);

        try (val response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() != 201) {
                throw new BadRequestException("error.user.registration");
            }

            val userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            val passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(requestDTO.getPassword());

            keycloak.realm(realm).users().get(userId).resetPassword(passwordCred);

            return userId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }

    private CyctiusUserDTO registerUserInternal(
            final UserRegistrationRequestDTO requestDTO,
            final String issuerId
    ) {
        val userDTO = CyctiusUserDTO.builder()
                .issuerId(issuerId)
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .mobile(requestDTO.getMobile())
                .ftp(requestDTO.getFtp())
                .maxHR(requestDTO.getMaxHR())
                .restHR(requestDTO.getRestHR())
                .build();

        try {
            val user = userRepository.save(userTransformer.transformToEntity(userDTO));

            return userTransformer.transformToDTO(user);
        } catch (ConstraintViolationException e) {
            throw new BadRequestException("error.user.registration.constraint");
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }
}
