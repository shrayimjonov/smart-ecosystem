package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CashFlowType implements Serializable {
    INCOME_MONEY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Приход денег";
                case cyrillic -> "Приход денег";
                default -> "Приход денег";
            };
        }
    },
    SPEND_MONEY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Расход денег";
                case cyrillic -> "Расход денег";
                default -> "Расход денег";
            };
        }
    },
    RETURN_INCOME_MONEY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Возврат прихода денег";
                case cyrillic -> "Возврат прихода денег";
                default -> "Возврат прихода денег";
            };
        }
    },
    RETURN_SPENT_MONEY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Возврат потраченных денег";
                case cyrillic -> "Возврат потраченных денег";
                default -> "Возврат потраченных денег";
            };
        }
    };

    CashFlowType() {
        this.name = this.getName();
        this.code = this.name();
    }

    private final String name;

    private final String code;

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    public static CashFlowType forValue(Map<String, String> value) {
        return CashFlowType.valueOf(value.get("code"));
    }

}
