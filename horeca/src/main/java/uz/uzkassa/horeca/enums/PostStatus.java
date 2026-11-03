package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Map;

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
                case en -> "Draft";
                default -> "Черновик";
            };
        }
    },
    PUBLISHED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chop etilgan";
                case cyrillic -> "Чоп этилган";
                case en -> "Published";
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
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public PostStatus forCodeValue(Map<String, String> value) {
        return PostStatus.valueOf(value.get("code"));
    }
}
