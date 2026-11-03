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
public enum PaymentType {

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
    HUMO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Humo";
                case uz_cyrillic -> "Ҳумо";
                default -> "Хумо";
            };
        }
    },
    UZ_CARD {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Uzkard";
                case uz_cyrillic -> "Узкард";
                default -> "Узкард";
            };
        }
    },
    CLICK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Click";
                case uz_cyrillic -> "Click";
                default -> "Click";
            };
        }
    },
    PAY_ME {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Payme";
                case uz_cyrillic -> "Payme";
                default -> "Payme";
            };
        }
    },
    A_PAY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "A-PAY";
                case uz_cyrillic -> "A-PAY";
                default -> "A-PAY";
            };
        }
    },
    UZ_CARD_MASTERCARD {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Uzcard - Mastercard";
                case uz_cyrillic -> "Uzcard - MasterCard";
                default -> "Uzcard - MasterCard";
            };
        }
    },
    HUMO_VISA {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUMO - VISA";
                case uz_cyrillic -> "HUMO - VISA";
                default -> "HUMO - VISA";
            };
        }
    },
    HUMO_TRASTBANK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUMO - TRASTBANK";
                case uz_cyrillic -> "HUMO - TRASTBANK";
                default -> "HUMO - TRASTBANK";
            };
        }
    },
    HUMO_HAMKORBANK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUMO - HAMKORBANK";
                case uz_cyrillic -> "HUMO - HAMKORBANK";
                default -> "HUMO - HAMKORBANK";
            };
        }
    };

    private final String name;
    private final String code;

    PaymentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static PaymentType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static PaymentType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PaymentTypeEnum");
    }
}
