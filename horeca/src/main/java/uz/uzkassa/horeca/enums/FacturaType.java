package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum FacturaType implements Serializable {
    STANDART(0) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Standard";
                case cyrillic:
                    return "Стандарт";
                default:
                    return "Стандартный";
            }
        }
    },
    ADDITIONAL(1) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Qo`shimcha";
                case cyrillic:
                    return "Қўшимча";
                default:
                    return "Дополнительный";
            }
        }
    },
    EXPENSE_COVER(2) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Xarajatlarni qoplash";
                case cyrillic:
                    return "Харажатларни қоплаш";
                default:
                    return "Возмещение расходов";
            }
        }
    },
    WITHOUT_PAYMENT(3) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "To`lovsiz";
                case cyrillic:
                    return "Тўловсиз";
                default:
                    return "Без оплаты";
            }
        }
    },
    EDITED(4) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Tuzatuvchi";
                case cyrillic:
                    return "Тузатувчи";
                default:
                    return "Исправленный";
            }
        }
    };

    private final Integer id;
    private final String name;
    private final String code;

    FacturaType(Integer id) {
        this.id = id;
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static FacturaType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown FacturaType enum value: " + code));
    }

    @JsonCreator
    public static FacturaType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for FacturaType");
    }

    public static FacturaType buildById(Integer id) {
        if (id == null) {
            return FacturaType.STANDART;
        }
        return Arrays.stream(values()).filter(invoiceSubType -> invoiceSubType.id.equals(id)).findFirst().orElse(FacturaType.STANDART);
    }
}
