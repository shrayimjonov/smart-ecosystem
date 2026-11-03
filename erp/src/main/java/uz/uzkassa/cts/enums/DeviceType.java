package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DeviceType implements Serializable {
    TERMINAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Terminal";
                case uz_cyrillic -> "Терминал";
                default -> "Терминальный";
            };
        }
    },
    FISCAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Fiskal";
                case uz_cyrillic -> "Фискал";
                default -> "Фискалкальный";
            };
        }
    },
    SOFTWARE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Dasturiy ta'minot";
                case uz_cyrillic -> "Дастурий таъминот";
                default -> "Программное обеспечение";
            };
        }
    },
    TERMINAL_FISCAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Terminal va Fiskal";
                case uz_cyrillic -> "Терминал ва Фискал";
                default -> "Терминальный и Фискалкальный";
            };
        }
    },
    PHYSICAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Jismoniy";
                case uz_cyrillic -> "Жисмоний";
                default -> "Физический";
            };
        }
    },
    VIRTUAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Virtual";
                case uz_cyrillic -> "Виртуал";
                default -> "Виртуальный";
            };
        }
    };

    private final String name;
    private final String code;

    DeviceType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DeviceType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerBranchTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static DeviceType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerBranchTypeEnum");
    }
}
