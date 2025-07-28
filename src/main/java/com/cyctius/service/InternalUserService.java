package com.cyctius.service;

import com.cyctius.dto.CyctiusUserDTO;

public interface InternalUserService {
    CyctiusUserDTO updateUserData(CyctiusUserDTO userDTO);
    CyctiusUserDTO getCurrentUser();
    CyctiusUserDTO findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    CyctiusUserDTO findByEmail(String email);
    CyctiusUserDTO findByIssuerId(String issuerId);
}
