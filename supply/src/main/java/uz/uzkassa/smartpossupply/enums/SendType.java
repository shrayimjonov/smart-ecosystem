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
 * Date: 02.06.2023 10:20
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum SendType implements Serializable {
    SMS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sms";
                default -> "Смс";
            };
        }
    },
    PUSH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "PUSH";
                default -> "ПУШ";
            };
        }
    };

    private final String name;
    private final String code;

    SendType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static SendType forValue(Map<String, String> value) {
        return SendType.valueOf(value.get("code"));
    }
}
