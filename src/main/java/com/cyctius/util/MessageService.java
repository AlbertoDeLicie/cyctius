package com.cyctius.util;

public interface MessageService {
    String getMessage(String messageCode);
    String getMessage(final String messageCode, final Object... args);
}