package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LanguageEnum implements Serializable {
    uz("uz", "O'zbek"),
    cyrillic("cyrillic", "Узбек"),
    ru("ru", "Русский");

    private final String code;
    private final String name;

    LanguageEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LanguageEnum get(String code) {
        if (StringUtils.isEmpty(code)) {
            return ru;
        }
        for (LanguageEnum statusEnum : LanguageEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum;
            }
        }
        return ru;
    }

}
