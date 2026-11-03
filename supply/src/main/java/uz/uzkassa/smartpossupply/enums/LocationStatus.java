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
public enum LocationStatus implements Serializable {
    ONLINE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "ONLINE";
                case cyrillic -> "ONLINE";
                default -> "ONLINE";
            };
        }
    },

    OFFLINE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "OFFLINE";
                case cyrillic -> "OFFLINE";
                default -> "OFFLINE";
            };
        }
    };

    private final String name;
    private final String code;

    LocationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    public static LocationStatus forValue(Map<String, Object> value) {
        if (value == null) return null;

        Object code = value.get("code");
        if (code == null) return null;

        return LocationStatus.valueOf(code.toString().toUpperCase());
    }

}
