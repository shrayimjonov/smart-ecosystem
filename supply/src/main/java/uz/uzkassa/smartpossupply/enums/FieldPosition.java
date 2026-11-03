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
 * Date: 19.01.2023 19:43
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum FieldPosition implements Serializable {

    CENTER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markaz";
                case cyrillic -> "Марказ";
                default -> "Центр";
            };
        }
    },
    RIGHT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'ng";
                case cyrillic -> "O'ng";
                default -> "Право";
            };
        }
    };

    private final String name;
    private final String code;

    FieldPosition() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static FieldPosition forValue(Map<String, String> value) {
        return FieldPosition.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
