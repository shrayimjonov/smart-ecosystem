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
public enum SerialNumberType implements Serializable {
    SELF_MADE {
        @Override
        public String getName(LanguageEnum language) {
            return "Самостоятельно";
        }
    },
    OPERATOR {
        @Override
        public String getName(LanguageEnum language) {
            return "Оператором";
        }
    };

    private final String name;
    private final String code;

    SerialNumberType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static SerialNumberType forValue(Map<String, String> value) {
        return SerialNumberType.valueOf(value.get("code"));
    }
}
