package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RepairTicketStatus {

    NEW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi";
                case uz_cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    TRANSFERRED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuborilgan";
                case uz_cyrillic -> "Юборилган";
                default -> "Передан";
            };
        }
    },
    IN_PROGRESS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Jarayonda";
                case uz_cyrillic -> "Процессда";
                default -> "В процессе";
            };
        }
    },
    PROCESSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qayta ishlangan";
                case uz_cyrillic -> "Қайта ишланган";
                default -> "Обработан";
            };
        }
    },
    TRANSFERRED_BACK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qaytarib yuborilgan";
                case uz_cyrillic -> "Қайтариб юборилган";
                default -> "Передан Обратно";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yopilgan";
                case uz_cyrillic -> "Ёпилган";
                default -> "Закрыт";
            };
        }
    };

    private final String name;
    private final String code;

    RepairTicketStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static RepairTicketStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown RepairTicketStatus enum value: " + code));
    }

    @JsonCreator
    public static RepairTicketStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for RepairTicketStatus");
    }
}
