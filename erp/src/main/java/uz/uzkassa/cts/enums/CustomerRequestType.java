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
public enum CustomerRequestType {
    NEW_CUSTOMER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi mijoz";
                case uz_cyrillic -> "Янги мижоз";
                default -> "Новый клиент";
            };
        }
    },
    NEW_BRANCH {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi filial";
                case uz_cyrillic -> "Янги филиал";
                default -> "Новый филиал";
            };
        }
    },
    COMPLAINT_CUSTOMER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Mijoz shikoyati";
                case uz_cyrillic -> "Мижоз шикояти";
                default -> "Жалоба клиента";
            };
        }
    },
    PRIME_LEASING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Prime bo'lib to'lash";
                case uz_cyrillic -> "Prime бўлиб тўлаш";
                default -> "Рассрочка Prime";
            };
        }
    },
    PRIME_PURCHASE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Prime xarid qilish";
                case uz_cyrillic -> "Prime харид қилиш";
                default -> "Покупка Prime";
            };
        }
    },
    TERMINAL_FISCAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Terminaldan Fiskalga";
                case uz_cyrillic -> "Терминалдан Фискалга";
                default -> "Терминальный на Фискальный";
            };
        }
    },
    FISCAL_TERMINAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Fiscaldan Terminalga";
                case uz_cyrillic -> "Фискалдан Терминалга";
                default -> "Фискальный на Терминальный";
            };
        }
    },
    PROMO_MANAGER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Promo menejeri";
                case uz_cyrillic -> "Промо Менежер";
                default -> "Промо-менеджер";
            };
        }
    };

    private final String name;
    private final String code;

    CustomerRequestType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CustomerRequestType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerRequestTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static CustomerRequestType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerRequestTypeEnum");
    }
}
