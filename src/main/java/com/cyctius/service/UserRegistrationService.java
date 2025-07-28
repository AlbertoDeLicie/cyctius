package com.cyctius.service;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.dto.UserRegistrationRequestDTO;

public interface UserRegistrationService {
    /**
     * Registers a new user with the provided details.
     *
     * @param requestDTO the user registration request containing user details
     * @return a CyctiusUserDTO representing the registered user
     */
    CyctiusUserDTO registerUser(UserRegistrationRequestDTO requestDTO);
}
