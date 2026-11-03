package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum DeliveryType implements Serializable {
    DELIVERY {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "Yetkazib berish";
            }
            return "Доставка";
        }
    },
    SELF_PICKUP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "O`zi olib ketish";
            }
            return "Самовывоз";
        }
    };

    private final String name;
    private final String code;

    DeliveryType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DeliveryType get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }

    @JsonCreator
    public static DeliveryType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DeliveryType");
    }
}
