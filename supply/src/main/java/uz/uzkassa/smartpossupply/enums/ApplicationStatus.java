package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ApplicationStatus implements Serializable {
    NEW {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.ru) {
                return "Новый";
            }
            return "Yangi";
        }
    },
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.ru) {
                return "В ожидании";
            }
            return "Kutilmoqda";
        }
    },
    APPROVED {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.ru) {
                return "Одобренный";
            }
            return "Tasdiqlangan";
        }
    };

    private final String name;
    private final String code;

    ApplicationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageEnum.ru);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ApplicationStatus forValue(Map<String, String> value) {
        return ApplicationStatus.valueOf(value.get("code"));
    }
}
