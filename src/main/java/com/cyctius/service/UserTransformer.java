package com.cyctius.service;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.entity.CyctiusUser;

public interface UserTransformer {
    CyctiusUserDTO transformToDTO(CyctiusUser user);
    CyctiusUser transformToEntity(CyctiusUserDTO userDTO);
}
