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
public enum AdjustmentRejectReason implements Serializable {
    WRONG_ADJUSTMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Noto`g`ri";
                case cyrillic -> "Нотўғри";
                default -> "Неправильная корректировка";
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

    AdjustmentRejectReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AdjustmentRejectReason forValue(Map<String, String> value) {
        return AdjustmentRejectReason.valueOf(value.get("code"));
    }
}
