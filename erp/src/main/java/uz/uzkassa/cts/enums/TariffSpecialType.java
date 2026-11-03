package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum TariffSpecialType {
    ONLY_FOR_ORDER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bu tarif faqat buyurtma uchun";
                case uz_cyrillic -> "Бу тариф факат буюртма учун";
                default -> "Этот тариф только для заказа";
            };
        }
    },
    USE_EVERYWHERE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xohlagan joyda ishlatish mumkin";
                case uz_cyrillic -> "Хоҳлаган жойда ишлатиш мумкин";
                default -> "Можете использовать везде";
            };
        }
    };

    private final String code;
    private final String name;

    TariffSpecialType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static TariffSpecialType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown TariffSpecialType enum value: " + code));
    }

    @JsonCreator
    public static TariffSpecialType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for TariffSpecialType");
    }
}
