package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ServiceType {
    SALE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sotuv";
                case uz_cyrillic -> "Сотув";
                default -> "Продажа";
            };
        }
    },
    SERVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xizmat";
                case uz_cyrillic -> "Хизмат";
                default -> "Обслуживание";
            };
        }
    },
    REPAIR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ta'mirlash";
                case uz_cyrillic -> "Таъмирлаш";
                default -> "Ремонт";
            };
        }
    };

    private final String code;
    private final String name;

    ServiceType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ServiceType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ServiceType enum value: " + code));
    }

    @JsonCreator
    public static ServiceType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ServiceType");
    }
}
