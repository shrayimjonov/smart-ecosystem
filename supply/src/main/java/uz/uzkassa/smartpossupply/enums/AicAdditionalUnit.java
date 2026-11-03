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
 * Date: 05.08.2024
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum AicAdditionalUnit implements Serializable {
    UNIT(796) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Dona";
                case cyrillic -> "Дона";
                default -> "Шт";
            };
        }
    },
    UNIT_1000(798) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "1000 dona";
                case cyrillic -> "1000 дона";
                default -> "1000 шт";
            };
        }
    },
    LITRE(112) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Litr";
                case cyrillic -> "Литр";
                default -> "Литр";
            };
        }
    },
    LITRE_100(831) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Litr 100% spirt";
                case cyrillic -> "Литр 100% спирт";
                default -> "Литр 100% спирта";
            };
        }
    },
    NONE(null) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yo'q";
                case cyrillic -> "Ёқ";
                default -> "Отсутствует";
            };
        }
    };

    private final String name;
    private final String code;
    private final Integer unitCode;

    AicAdditionalUnit(Integer unitCode) {
        this.unitCode = unitCode;
        this.name = getName();
        this.code = this.name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AicAdditionalUnit forValue(Map<String, String> value) {
        return AicAdditionalUnit.valueOf(value.get("code"));
    }
}
