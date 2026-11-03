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
public enum ArrivalStatus implements Serializable { // order is important whenever adding new ArrivalStatus because it is used with ordinal in some entities
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
    SENT_BACK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qaytarib yuborildi";
                case uz_cyrillic -> "Қайтариб юборилди";
                default -> "Обратно отправлен";
            };
        }
    },
    CHANGED_CTO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CTO o`zgartirildi";
                case uz_cyrillic -> "ЦТО узгартирилди";
                default -> "Сменил ЦТО";
            };
        }
    },
    LEFT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ketdi";
                case uz_cyrillic -> "Келди";
                default -> "Ушел";
            };
        }
    };

    private final String code;
    private final String name;

    ArrivalStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ArrivalStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ArrivalStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static ArrivalStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ArrivalStatusEnum");
    }

    public static ArrivalStatus fromRussianToArrivalStatusEnum(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return switch (value.trim()) {
            case "Пришел" -> CAME;
            case "Не пришел" -> NOT_COME;
            case "Обратно отправлен" -> SENT_BACK;
            case "Сменил ЦТО" -> CHANGED_CTO;
            default -> throw new IllegalArgumentException("Unknown Russian ArrivalStatus value: " + value);
        };
    }

}
