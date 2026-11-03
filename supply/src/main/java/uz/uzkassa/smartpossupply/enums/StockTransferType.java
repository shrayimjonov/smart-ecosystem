package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.11.2024
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum StockTransferType implements Serializable {
    W2W {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ombordan omborga";
                case cyrillic -> "Омбордан омборга";
                default -> "Со склада на склад";
            };
        }
    },
    W2C {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ombordan mashinaga";
                case cyrillic -> "Омбордан машинага";
                default -> "Со склада в машину";
            };
        }
    },
    C2W {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mashinadan omborga";
                case cyrillic -> "Машинадан омборга";
                default -> "С машины на склад";
            };
        }
    },
    C2C {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mashinadan mashinaga";
                case cyrillic -> "Машинадан машинага";
                default -> "С машины в машину";
            };
        }
    };

    private final String name;
    private final String code;

    StockTransferType() {
        this.code = name();
        this.name = getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static Vat forValue(Map<String, String> value) {
        return Vat.valueOf(value.get("code"));
    }
}
