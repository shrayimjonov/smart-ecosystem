package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApplicationStatus {
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
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ApplicationStatus forValue(Map<String, String> value) {
        return ApplicationStatus.valueOf(value.get("code"));
    }
}
