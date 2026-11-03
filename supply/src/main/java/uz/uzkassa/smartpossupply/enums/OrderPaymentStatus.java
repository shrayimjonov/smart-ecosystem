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
public enum OrderPaymentStatus implements Serializable {
    WAITING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To`lov kutilmoqda";
                case cyrillic -> "Тўлов кутилмоқда";
                default -> "Ожидается оплата";
            };
        }
    },
    PARTIALLY_PAID {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qisman to`langan";
                case cyrillic -> "Қисман тўланган";
                default -> "Частично оплачено";
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
    };

    private final String name;
    private final String code;

    OrderPaymentStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static OrderPaymentStatus forValue(Map<String, String> value) {
        return OrderPaymentStatus.valueOf(value.get("code"));
    }
}
