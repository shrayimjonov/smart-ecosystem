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
public enum StockStatus implements Serializable {
    CHECKING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tekshiruvda";
                case cyrillic -> "Текширувда";
                default -> "На проверке";
            };
        }
    },
    CHECKING_ERROR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tekshiruvda xatolik";
                case cyrillic -> "Текширувда хатолик";
                default -> "Коды с ошибками";
            };
        }
    },
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
    CANCELLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bekor qilingan";
                case cyrillic -> "Бекор қилинган";
                default -> "Отменен";
            };
        }
    },

    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bajarildi";
                case cyrillic -> "Бажарилди";
                default -> "Завершен";
            };
        }
    },
    DELETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O`chirilgan";
                case cyrillic -> "Ўчирилган";
                default -> "Удалено";
            };
        }
    };

    private final String name;
    private final String code;

    StockStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static StockStatus forValue(Map<String, String> value) {
        return StockStatus.valueOf(value.get("code"));
    }
}
