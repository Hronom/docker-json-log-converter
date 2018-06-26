package com.github.hronom.dockerjsonlogconverter.components;

import com.vaadin.flow.i18n.I18NProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class SimpleI18NProvider implements I18NProvider {
    private final MessageSource messageSource;

    @Autowired
    public SimpleI18NProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return Collections.unmodifiableList(
            Arrays.asList(Locale.ENGLISH, Locale.CHINESE));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        return messageSource.getMessage(key, params, locale);
    }

}
