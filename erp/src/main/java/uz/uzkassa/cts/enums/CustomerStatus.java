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
public enum CustomerStatus {
    ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol";
                case uz_cyrillic -> "Фаол";
                default -> "Активный";
            };
        }
    },
    IN_ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol emas";
                case uz_cyrillic -> "Фаол эмас";
                default -> "Неактивный";
            };
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko'rib chiqilmoqda";
                case uz_cyrillic -> "Кўриб чиқилмоқда";
                default -> "В ожидании";
            };
        }
    },
    DRAFT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qoralama";
                case uz_cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    },
    TIN_CHANGED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "INN o'zgargan";
                case uz_cyrillic -> "ИНН ўзгарган";
                default -> "ИНН изменен";
            };
        }
    },
    MISSING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yo'qolgan";
                case uz_cyrillic -> "Йўқолган";
                default -> "Отсутствующий";
            };
        }
    },
    DEBTOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qarzdor";
                case uz_cyrillic -> "Қарздор";
                default -> "Дебитор";
            };
        }
    },
    REFUSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad etilgan";
                case uz_cyrillic -> "Рад этилган";
                default -> "Отказавшийся";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yopiq";
                case uz_cyrillic -> "Ёпиқ";
                default -> "Закрыт";
            };
        }
    },
    LIQUIDATED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tugatilgan";
                case uz_cyrillic -> "Тугатилган";
                default -> "Ликвидирован";
            };
        }
    };

    private final String name;
    private final String code;

    CustomerStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CustomerStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static CustomerStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerStatusEnum");
    }
}