package com.cyctius.handler.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExpiredException extends RuntimeException {
    private final String message;

    public ExpiredException(String message) {
        this.message = message;
    }
}