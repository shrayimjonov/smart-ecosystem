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
public enum StockAdjustmentStatus implements Serializable {
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

    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bajarildi";
                case cyrillic -> "Бажарилди";
                default -> "Завершено";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rad etilgan";
                case cyrillic -> "Рад этилган";
                default -> "Отклонено";
            };
        }
    },
    SENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Muomaladan chiqarilgan";
                case cyrillic -> "Муомаладан чиқарилган";
                default -> "Выведен из оборота";
            };
        }
    };

    private final String name;
    private final String code;

    StockAdjustmentStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static StockAdjustmentStatus forValue(Map<String, String> value) {
        return StockAdjustmentStatus.valueOf(value.get("code"));
    }
}
