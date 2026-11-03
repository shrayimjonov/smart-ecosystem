package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum LanguageEnum implements Serializable {
    uz("uz", "O'zbek"),
    cyrillic("cyrillic", "Узбек"),
    en("en", "English"),
    ru("ru", "Русский");

    private final String code;
    private final String name;

    LanguageEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LanguageEnum get(String code) {
        if (StringUtils.isEmpty(code)) {
            return getDefaultLanguage();
        }
        for (LanguageEnum language : LanguageEnum.values()) {
            if (code.equals(language.getCode())) {
                return language;
            }
        }
        return getDefaultLanguage();
    }

    public static LanguageEnum getDefaultLanguage() {
        return LanguageEnum.ru;
    }
}
