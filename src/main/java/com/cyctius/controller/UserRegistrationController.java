package com.cyctius.controller;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.dto.UserRegistrationRequestDTO;
import com.cyctius.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<CyctiusUserDTO> registerUser(@RequestBody @Valid final UserRegistrationRequestDTO requestDTO) {
        return ResponseEntity.ok(userRegistrationService.registerUser(requestDTO));
    }

}
