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
 * Date: 07.11.2023
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CommentType implements Serializable {
    CHAT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chat";
                case cyrillic -> "Чат";
                default -> "Чат";
            };
        }
    },
    APPLICATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Application";
                case cyrillic -> "Апплиcатион";
                default -> "Апплиcатион";
            };
        }
    };

    private final String name;
    private final String code;

    CommentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static CommentType forValue(Map<String, String> value) {
        return CommentType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
