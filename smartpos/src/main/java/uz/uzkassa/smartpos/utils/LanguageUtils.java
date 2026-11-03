package uz.uzkassa.smartpos.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;

import java.util.Locale;

public final class LanguageUtils {
    public static LanguageEnum getLanguage() {
        Locale locale = LocaleContextHolder.getLocale();
        if (Locale.of("ru").equals(locale)) return LanguageEnum.ru;
        if (Locale.of("uz").equals(locale)) return LanguageEnum.uz;
        if (Locale.of("cyrl").equals(locale)) return LanguageEnum.cyrillic;
        return LanguageEnum.ru;
    }
}
