package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptStatusDTO;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ReceiptStatus implements Serializable {
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                case en -> "Draft";
                default -> "Черновик";
            };
        }
    },
    PAID {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To'langan";
                case cyrillic -> "Тўланган";
                case en -> "Paid";
                default -> "Оплачен";
            };
        }
    },
    RETURNED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaytish";
                case cyrillic -> "Қайтиш";
                case en -> "Return";
                default -> "Возврат";
            };
        }
    },
    INCOME {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Naqd depozit";
                case cyrillic -> "Нақд депозит";
                case en -> "Cash deposit";
                default -> "Внесение наличности";
            };
        }
    },
    WITHDRAW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Depozitni qaytarish";
                case cyrillic -> "Депозитни қайтариш";
                case en -> "Withdraw";
                default -> "Возврат внесение";
            };
        }
    },
    FLOW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chiqim";
                case cyrillic -> "Чиқим";
                case en -> "Flow";
                default -> "Расход";
            };
        }
    },
    RETURN_FLOW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chiqimning qaytishi";
                case cyrillic -> "Чиқимнинг қайтиши";
                case en -> "Return flow";
                default -> "Возврат расхода";
            };
        }
    },
    INCASSATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case en -> "In cassation";
                default -> "Инкассация";
            };
        }
    },
    ADVANCE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Avans";
                case cyrillic -> "Аванс";
                case en -> "Advance";
                default -> "Аванс";
            };
        }
    },
    CREDIT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kredit";
                case cyrillic -> "Кредит";
                case en -> "Credit";
                default -> "Кредит";
            };
        }
    },
    UNKNOWN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Test";
                case cyrillic -> "Тест";
                case en -> "Test";
                default -> "Тестовый";
            };
        }
    };

    private final String name;
    private final String code;

    ReceiptStatus() {
        this.code = this.name();
        this.name = getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static ReceiptStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ReceiptStatus enum value: " + code));
    }

    @JsonCreator
    public static ReceiptStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ReceiptStatus");
    }

    public static ReceiptStatus getByCode(String code) {
        return Arrays.stream(ReceiptStatus.values()).filter(shapeType -> shapeType.getCode().equals(code)).findFirst().orElse(null);
    }

    public static TerminalReceiptStatusDTO toTerminalStatus(ReceiptStatus status) {
        return new TerminalReceiptStatusDTO(status.getName(LanguageEnum.ru), status.getName(LanguageEnum.uz), status.getCode());
    }

    public boolean isFiscalizable(final boolean fromInvoiceOrVoucher) {
        return RETURNED == this || (EnumSet.of(PAID, CREDIT, ADVANCE).contains(this) && fromInvoiceOrVoucher);
    }

    public boolean isFiscalized() {
        return EnumSet.of(PAID, RETURNED, CREDIT, ADVANCE).contains(this);
    }
}
