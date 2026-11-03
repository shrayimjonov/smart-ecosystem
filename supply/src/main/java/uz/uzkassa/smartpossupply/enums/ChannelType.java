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
public enum ChannelType implements Serializable {
    PUSH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "PUSH";
                case cyrillic -> "PUSH";
                default -> "PUSH";
            };
        }
    },
    NOTIFICATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "NOTIFICATION";
                case cyrillic -> "NOTIFICATION";
                default -> "NOTIFICATION";
            };
        }
    },
    SMS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "SMS";
                case cyrillic -> "SMS";
                default -> "SMS";
            };
        }
    };

    private final String name;
    private final String code;

    ChannelType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ChannelType forValue(Map<String, String> value) {
        return ChannelType.valueOf(value.get("code"));
    }
}
