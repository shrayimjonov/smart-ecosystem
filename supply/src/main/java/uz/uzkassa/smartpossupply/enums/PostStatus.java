package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.10.2022 14:50
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PostStatus implements Serializable {
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    },
    IN_MODERATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'zgartirishda";
                case cyrillic -> "Ўзгартиришда";
                default -> "На модерации";
            };
        }
    },
    PUBLISHED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chop etilgan";
                case cyrillic -> "Чоп этилган";
                default -> "Опубликовано";
            };
        }
    };

    private final String name;
    private final String code;

    PostStatus() {
        this.name = this.name();
        this.code = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
