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
public enum HumoConnectOperatorStatus {
    CONNECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ulangan";
                case uz_cyrillic -> "Уланган";
                default -> "Подключен";
            };
        }
    },
    NOT_CONNECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ulanmagan";
                case uz_cyrillic -> "Уланмаган";
                default -> "Не подключен";
            };
        }
    },
    DEFAULT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Birlamchi";
                case uz_cyrillic -> "Бирламчи";
                default -> "По умолчанию";
            };
        }
    };

    private final String code;
    private final String name;

    HumoConnectOperatorStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public static HumoConnectOperatorStatus fromOrdinal(int ordinal) {
        HumoConnectOperatorStatus[] values = HumoConnectOperatorStatus.values();
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
        return values[ordinal];
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static HumoConnectOperatorStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown HumoConnectOperatorStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static HumoConnectOperatorStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for HumoConnectOperatorStatusEnum");
    }
}
