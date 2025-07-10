package com.cyctius.util.impl;

import com.cyctius.util.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MessageServiceImpl implements MessageService {
    private final String DEFAULT_LNG = "en";
    private final MessageSource messageSource;

    public String getMessage(String messageCode) {
        String resultMessage = "";
        try {
            resultMessage = messageSource
                .getMessage(messageCode, null, LocaleContextHolder.getLocale());
        } catch (final Exception ex) {
            resultMessage = messageSource
                .getMessage(messageCode, null, Locale.forLanguageTag(DEFAULT_LNG));
        }

        return resultMessage;
    }

    public String getMessage(
        final String messageCode,
        final Object... args
    ) {
        String resultMessage = "";

        try {
            resultMessage = messageSource
                .getMessage(messageCode, args, LocaleContextHolder.getLocale());
        } catch (final Exception ex) {
            resultMessage = messageSource
                .getMessage(messageCode, args, Locale.forLanguageTag(DEFAULT_LNG));
        }
        return resultMessage;
    }
}