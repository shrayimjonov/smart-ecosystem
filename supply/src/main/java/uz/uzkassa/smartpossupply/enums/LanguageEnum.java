package uz.uzkassa.smartpossupply.enums;

import java.io.Serializable;


public enum LanguageEnum implements Serializable {
    uz,
    cyrillic,
    ru;

    public static LanguageEnum getDefaultLanguage() {
        return LanguageEnum.ru;
    }

    public static LanguageEnum getByCode(String code) {
        for (LanguageEnum language : LanguageEnum.values()) {
            if (language.name().equals(code)) {
                return language;
            }
        }
        return getDefaultLanguage();
    }
}
