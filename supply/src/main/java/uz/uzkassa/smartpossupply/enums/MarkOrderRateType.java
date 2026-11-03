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
public enum MarkOrderRateType implements Serializable {

    ZERO(0) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Nol";
                case cyrillic -> "Нол";
                default -> "Нулевой";
            };
        }
    },
    STANDARD(1) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Standart";
                case cyrillic -> "Стандарт";
                default -> "Стандартный";
            };
        }
    };

    private final String name;
    private final String code;
    private final int rate;

    MarkOrderRateType(int rate) {
        this.rate = rate;
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static MarkOrderRateType forValue(Map<String, String> value) {
        return MarkOrderRateType.valueOf(value.get("code"));
    }
}
