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
 * Date: 07.02.2024
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum ValueType implements Serializable {

    NUMBER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Raqam";
                case cyrillic -> "Рақам";
                default -> "Числовой";
            };
        }
    },
    TEXT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Matn";
                case cyrillic -> "Матн";
                default -> "Текстовый";
            };
        }
    },
    BOOLEAN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mantiqiy";
                case cyrillic -> "Мантиқий";
                default -> "Логическое значение";
            };
        }
    },
    DATE_TIME {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sana va vaqt";
                case cyrillic -> "Сана ва вақт";
                default -> "Дата и время";
            };
        }
    };

    private final String name;
    private final String code;

    ValueType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ValueType forValue(Map<String, String> value) {
        return ValueType.valueOf(value.get("code"));
    }
}
