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
public enum AgeGroup implements Serializable {
    TEENAGERS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'smirlar";
                case cyrillic -> "Ўсмирлар";
                case en -> "Teenagers";
                default -> "Подростки";
            };
        }
    },
    ADULTS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yoshlar";
                case cyrillic -> "Ёшлар";
                case en -> "Adults";
                default -> "Молодёжь";
            };
        }
    },
    MIDDLE_AGED_ADULTS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'rta yoshdagilar";
                case cyrillic -> "Ўрта ёшдагилар";
                case en -> "Middle aged adults";
                default -> "Средний возраст";
            };
        }
    },
    ELDERLY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Katta yoshdagilar";
                case cyrillic -> "Катта ёшдагилар";
                case en -> "Elderly";
                default -> "Пожилые люди";
            };
        }
    };

    private final String name;

    private final String code;

    AgeGroup() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static AgeGroup get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown AgeGroup enum value: " + code));
    }

    @JsonCreator
    public static AgeGroup fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for AgeGroup");
    }
}
