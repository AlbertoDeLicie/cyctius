package com.cyctius.handler.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BadRequestException extends RuntimeException {
    private final String message;

    public BadRequestException(final String message) {
        this.message = message;
    }
}