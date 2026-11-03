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
import java.util.Map;

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
                case en -> "Event";
                default -> "Мероприятие";
            };
        }
    },
    ARTICLE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Maqola";
                case cyrillic -> "Мақола";
                case en -> "Article";
                default -> "Статья";
            };
        }
    },
    NEWS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangilik";
                case cyrillic -> "Янгилик";
                case en -> "News";
                default -> "Новости";
            };
        }
    };

    private final String name;
    private final String code;

    PostType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static PostType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PostType enum value: " + code));
    }

    @JsonCreator
    public static PostType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PostType");
    }
}
