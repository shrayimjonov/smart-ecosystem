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
public enum DocFlowStatus implements Serializable {
    CAME {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Keldi";
                case uz_cyrillic -> "Келди";
                default -> "Пришел";
            };
        }
    },
    NOT_COME {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kelmadi";
                case uz_cyrillic -> "Келмади";
                default -> "Не пришел";
            };
        }
    },
    DEFECT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kamchiligi bor";
                case uz_cyrillic -> "Камчилиги бор";
                default -> "Дефект";
            };
        }
    },
    TRANSFERRED_BANK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bankka o'tkazildi";
                case uz_cyrillic -> "Банкка ўтказилди";
                default -> "Передан в Банк";
            };
        }
    },
    TRANSFERRED_ARCHIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Arxivga o'tkazildi";
                case uz_cyrillic -> "Архивга ўтказилди";
                default -> "Передан в архив";
            };
        }
    };

    private final String code;
    private final String name;

    DocFlowStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DocFlowStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown DocFlowStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static DocFlowStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DocFlowStatusEnum");
    }
}
