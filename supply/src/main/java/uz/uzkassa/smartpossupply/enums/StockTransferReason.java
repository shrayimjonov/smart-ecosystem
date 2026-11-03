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
public enum StockTransferReason implements Serializable {
    DAMAGED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Shikastlangan";
                case cyrillic -> "Шикастланган";
                default -> "Поврежден";
            };
        }
    },

    EXPIRED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Muddati o'tgan";
                case cyrillic -> "Муддати ўтган";
                default -> "Просрочен";
            };
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa";
                case cyrillic -> "Бошқа";
                default -> "Другой";
            };
        }
    };

    private final String name;
    private final String code;

    StockTransferReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static StockTransferReason forValue(Map<String, String> value) {
        return StockTransferReason.valueOf(value.get("code"));
    }
}
