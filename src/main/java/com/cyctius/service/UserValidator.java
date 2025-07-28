package com.cyctius.service;

import com.cyctius.handler.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserValidator {

    private final InternalUserService internalUserService;

    public void validateAuthor(Object content, String authorFieldName) {
        try {
            val userIdField = content.getClass().getDeclaredField(authorFieldName);
            val user = internalUserService.getCurrentUser();

            if (userIdField.getType() != String.class) {
                throw new IllegalArgumentException(
                        "Field 'userId' must be of type String in content"
                );
            }

            userIdField.setAccessible(true);
            String userId = (String) userIdField.get(content);

            if (userId == null || !userId.equals(user.getUserId())) {
                throw new BadRequestException("error.user.not.authorized");
            }

        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(
                    "Content must have a field 'userId' to validate author"
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
