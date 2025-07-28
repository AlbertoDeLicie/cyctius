package com.cyctius.service;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.dto.UpdateUserDataRequestDTO;

public interface UserDataService {
    CyctiusUserDTO updateUserData(UpdateUserDataRequestDTO requestDTO);
    CyctiusUserDTO currentUserData();
}
