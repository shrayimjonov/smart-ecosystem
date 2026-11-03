package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Language implements Serializable {

    ru("ru", "Русский"),
    uz_cyrillic("uz_Cyrl_UZ", "Ўзбек"),
    uz_latin("uz_Latn_UZ", "O'zbek");

    private final String code;
    private final String name;

    Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Language get(String code) {
        if (code == null || code.isEmpty()) {
            return getDefaultLanguage();
        }
        for (Language language : Language.values()) {
            if (code.equals(language.getCode())) {
                return language;
            }
        }
        return getDefaultLanguage();
    }

    public static Language getDefaultLanguage() {
        return Language.ru;
    }
}
