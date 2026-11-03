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
public enum UserStatus implements Serializable {
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

    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faollashmagan";
                case cyrillic -> "Фаоллашмаган";
                default -> "В ожидании";
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

    INACTIVE {
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

    UserStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static UserStatus forValue(Map<String, String> value) {
        return UserStatus.valueOf(value.get("code"));
    }
}
