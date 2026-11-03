package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2023 19:11
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CurrencyType implements Serializable {

    UZS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case ru -> "Сум";
                case cyrillic -> "Сўм";
                default -> "So`m";
            };
        }
    },
    USD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case ru -> "Доллар";
                case cyrillic -> "Доллар";
                default -> "Dollar";
            };
        }
    },
    RUB {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case ru -> "Рубль";
                case cyrillic -> "Рубл";
                default -> "Rubl";
            };
        }
    };

    private final String name;
    private final String code;

    CurrencyType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static CurrencyType forValue(Map<String, String> value) {
        return CurrencyType.valueOf(value.get("code"));
    }

}
