package com.cyctius.service.impl;

import com.cyctius.dto.CyctiusUserDTO;
import com.cyctius.dto.UpdateUserDataRequestDTO;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.service.UserDataService;
import com.cyctius.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDataServiceImpl implements UserDataService {
    private final InternalUserService internalUserService;

    @Override
    public CyctiusUserDTO updateUserData(final UpdateUserDataRequestDTO requestDTO) {
        if (Objects.isNull(requestDTO)) {
            throw new BadRequestException("error.user.update.null");
        }

        val existingUserData = internalUserService.getCurrentUser();
        val edited = mergeNotNullData(requestDTO, existingUserData);

        return internalUserService.updateUserData(edited);
    }

    @Override
    public CyctiusUserDTO currentUserData() {
        return internalUserService.getCurrentUser();
    }

    private CyctiusUserDTO mergeNotNullData(
            final UpdateUserDataRequestDTO requestDTO,
            CyctiusUserDTO existingUserData
    ) {
        val fields = UpdateUserDataRequestDTO.class.getDeclaredFields();

        for (val field : fields) {
            field.setAccessible(true);
            try {
                val value = field.get(requestDTO);
                if (value != null) {
                    // ищем соответствующее поле в existingUserData
                    val targetField = CyctiusUserDTO.class.getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(existingUserData, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Ошибка при копировании поля: " + field.getName(), e);
            }
        }

        return existingUserData;
    }
}
