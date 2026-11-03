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
public enum AccountType implements Serializable {
    EXTERNAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tashqi";
                case cyrillic -> "Ташқи";
                default -> "Внешний";
            };
        }
    },
    STOCK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Stock";
                case cyrillic -> "Сток";
                default -> "Сток";
            };
        }
    },
    WEB_KASSA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Web kassa";
                case cyrillic -> "Веб касса";
                default -> "Веб касса";
            };
        }
    },
    WHOLESALE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ulgurchi";
                case cyrillic -> "Улгурчи";
                default -> "Оптом";
            };
        }
    };

    private final String name;
    private final String code;

    AccountType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AccountType forValue(Map<String, String> value) {
        return AccountType.valueOf(value.get("code"));
    }
}
