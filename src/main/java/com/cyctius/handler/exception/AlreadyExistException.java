package com.cyctius.handler.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AlreadyExistException extends RuntimeException {
    private final String message;
    private Object object;

    public AlreadyExistException(final String message) {
        this.message = message;
        object = null;
    }
}