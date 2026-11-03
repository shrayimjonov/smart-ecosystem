package uz.uzkassa.silen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocalizationService {

    private static ResourceBundleMessageSource messageSource;

    @Autowired
    LocalizationService(ResourceBundleMessageSource messageSource) {
        LocalizationService.messageSource = messageSource;
    }

    public String getMessage(String msgCode, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, args, locale);
    }

}
