package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;

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
                case en -> "Text";
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
                case en -> "Photo";
                default -> "Фото";
            };
        }
    },
    PHOTO_GALLERY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Foto galereya";
                case cyrillic -> "Фото галерея";
                case en -> "Photo gallery";
                default -> "Фото галерея";
            };
        }
    },
    VIDEO {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz, en -> "Video";
                case cyrillic -> "Видео";
                default -> "Видео";
            };
        }
    },
    ADDRESS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Manzil";
                case cyrillic -> "Манзил";
                case en -> "Address";
                default -> "Адрес";
            };
        }
    };

    private final String name;
    private final String code;

    AttachmentType() {
        this.code = this.name();
        this.name = getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static AttachmentType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown AttachmentType enum value: " + code));
    }

    @JsonCreator
    public static AttachmentType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for AttachmentType");
    }

}
