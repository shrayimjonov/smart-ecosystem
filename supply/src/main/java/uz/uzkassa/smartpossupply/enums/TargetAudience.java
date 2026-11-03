package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum TargetAudience implements Serializable {
    MALE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Erkaklar";
                case cyrillic -> "Эркаклар";
                default -> "Мужчины";
            };
        }
    },
    FEMALE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ayollar";
                case cyrillic -> "Аёллар";
                default -> "Женщины";
            };
        }
    },
    ALL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hamma";
                case cyrillic -> "Ҳамма";
                default -> "Все";
            };
        }
    };


    private final String name;
    private final String code;

    TargetAudience() {
        this.code = this.name();
        this.name = getName();
    }

    public static TargetAudience get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }

    @JsonCreator
    public static TargetAudience fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for TargetAudience");
    }


    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
