package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum SaleType implements Serializable {
    PREPAYMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Oldindan to`lov";
                case cyrillic -> "Олдиндан тўлов";
                default -> "Предоплата";
            };
        }
    },
    REALIZATION {
        @Override
        public String getName(LanguageEnum language) {
            return "На реализацию";
        }
    };

    private final String name;
    private final String code;

    SaleType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static SaleType forValue(Map<String, String> value) {
        return SaleType.valueOf(value.get("code"));
    }
}
