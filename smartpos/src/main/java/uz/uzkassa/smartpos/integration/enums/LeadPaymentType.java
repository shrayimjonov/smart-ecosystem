package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LeadPaymentType {
    PURCHASE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sotib olish";
                case cyrillic -> "Сотиб олиш";
                default -> "Покупка";
            };
        }
    },
    LEASING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bo'lib to'lash";
                case cyrillic -> "Бўлиб тўлаш";
                default -> "Рассрочка";
            };
        }
    };

    private final String name;
    private final String code;

    LeadPaymentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    public static LeadPaymentType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown LeadPaymentType enum value: " + code));
    }

    @JsonCreator
    public static LeadPaymentType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for LeadPaymentType");
    }
}
