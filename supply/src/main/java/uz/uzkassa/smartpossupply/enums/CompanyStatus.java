package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CompanyStatus implements Serializable {
    ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol";
                case cyrillic -> "Фаол";
                default -> "Актив";
            };
        }
    },

    BLOCKED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bloklangan";
                case cyrillic -> "Блокланган";
                default -> "Заблокировано";
            };
        }
    },

    IN_ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol emas";
                case cyrillic -> "Фаол эмас";
                default -> "Неактивный";
            };
        }
    };

    private final String name;
    private final String code;

    CompanyStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static CompanyStatus forValue(Map<String, String> value) {
        return CompanyStatus.valueOf(value.get("code"));
    }
}
