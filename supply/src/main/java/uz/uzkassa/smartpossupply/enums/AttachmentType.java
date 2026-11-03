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
 * Date: 11.30.2022 18:06
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum AttachmentType implements Serializable {
    TEXT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Matn";
                case cyrillic -> "Матн";
                default -> "Текcт";
            };
        }
    },
    PHOTO {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rasm";
                case cyrillic -> "Расм";
                default -> "Фото";
            };
        }

    },
    PHOTO_GALLERY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Galereya";
                case cyrillic -> "Галерея";
                default -> "Галерея";
            };
        }
    };

    private final String name;
    private final String code;

    AttachmentType() {
        this.code = this.name();
        this.name = getName();
    }

    public static AttachmentType get(String folderType) {
        return AttachmentType.valueOf(folderType);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AttachmentType forValue(Map<String, String> value) {
        return AttachmentType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
