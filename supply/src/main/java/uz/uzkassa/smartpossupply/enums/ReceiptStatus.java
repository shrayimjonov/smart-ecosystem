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
 * Date: 12.19.2022
 */
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
                default -> "Оплачен";
            };
        }
    },
    PARTIALLY_PAID {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "PARTIALLY_PAID";
                case cyrillic -> "PARTIALLY_PAID";
                default -> "PARTIALLY_PAID";
            };
        }
    },
    RETURNED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaytarilgan";
                case cyrillic -> "Қайтарилган";
                default -> "Возврат";
            };
        }
    };

    private final String code;
    private final String name;

    ReceiptStatus() {
        this.name = this.getName();
        this.code = this.name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    public static ReceiptStatus forValue(Map<String, String> value) {
        return ReceiptStatus.valueOf(value.get("code"));
    }

    @Override
    public String toString() {
        return code;
    }
}
