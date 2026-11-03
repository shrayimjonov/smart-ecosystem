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
public enum TelegramBotType implements Serializable {
    SUPPLIER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "SUPPLIER";
                case cyrillic -> "SUPPLIER";
                default -> "SUPPLIER";
            };
        }
    },
    CUSTOMER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "CUSTOMER";
                case cyrillic -> "CUSTOMER";
                default -> "CUSTOMER";
            };
        }
    };

    private final String name;
    private final String code;

    TelegramBotType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    public static TelegramBotType fromJson(Map<String, Object> obj) {
        if (obj == null) return null;
        Object code = obj.get("code");
        if (code == null) return null;
        return TelegramBotType.valueOf(code.toString());
    }

}
