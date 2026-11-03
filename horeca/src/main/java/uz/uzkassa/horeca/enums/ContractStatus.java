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
public enum ContractStatus implements Serializable {
    ACCEPTED {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Tasdiqlangan";
                case cyrillic:
                    return "Тасдиқланган";
                default:
                    return "Подтверждено";
            }
        }
    },
    REJECTED {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Rad etilgan";
                case cyrillic:
                    return "Рад этилган";
                default:
                    return "Отклонено";
            }
        }
    },
    INACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Faol emas";
                case cyrillic:
                    return "Фаол эмас";
                default:
                    return "Неактивный";
            }
        }
    };

    private final String name;
    private final String code;

    ContractStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static ContractStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ContractStatus enum value: " + code));
    }

    @JsonCreator
    public static ContractStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ContractStatus");
    }
}
