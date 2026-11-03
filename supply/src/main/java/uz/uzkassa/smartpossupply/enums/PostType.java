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
 * Date: 02.11.2023
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PostType implements Serializable {
    EVENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tadbir";
                case cyrillic -> "Тадбир";
                default -> "Мероприятие";
            };
        }
    },
    ADVERTISEMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Reklama";
                case cyrillic -> "Реклама";
                default -> "Реклама";
            };
        }
    },
    NEWS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangilik";
                case cyrillic -> "Янгилик";
                default -> "Новости";
            };
        }
    },
    SYSTEM_UPGRADE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tizim yangilanishi";
                case cyrillic -> "Тизим янгиланиши";
                default -> "Обновление системы";
            };
        }
    };

    private final String name;
    private final String code;

    PostType() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PostType forValue(Map<String, String> value) {
        return PostType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
