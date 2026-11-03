package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

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
            return switch (language) {
                case uz -> "Standard";
                case cyrillic -> "Стандарт";
                default -> "Стандартный";
            };
        }
    },
    ADDITIONAL(1) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qo`shimcha";
                case cyrillic -> "Қўшимча";
                default -> "Дополнительный";
            };
        }
    },
    EXPENSE_COVER(2) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xarajatlarni qoplash";
                case cyrillic -> "Харажатларни қоплаш";
                default -> "Возмещение расходов";
            };
        }
    },
    WITHOUT_PAYMENT(3) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To`lovsiz";
                case cyrillic -> "Тўловсиз";
                default -> "Без оплаты";
            };
        }
    },
    EDITED(4) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tuzatuvchi";
                case cyrillic -> "Тузатувчи";
                default -> "Исправленный";
            };
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
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static FacturaType forValue(Map<String, String> value) {
        return FacturaType.valueOf(value.get("code"));
    }

    public static FacturaType buildById(Integer id) {
        if (id == null) {
            return FacturaType.STANDART;
        }
        return Arrays.stream(values()).filter(invoiceSubType -> invoiceSubType.id.equals(id)).findFirst().orElse(FacturaType.STANDART);
    }
}
