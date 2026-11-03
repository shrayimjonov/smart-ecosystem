package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KPIStatus {
    NEW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangi";
                case cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol";
                case cyrillic -> "Фаол";
                default -> "Активный";
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

    KPIStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
