package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerVatTypeEnum {
    WITH_VAT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "QQS BILAN";
                case uz_cyrillic -> "ҚҚС БИЛАН";
                default -> "С НДС";
            };
        }
    },
    WITHOUT_VAT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "QQSSIZ";
                case uz_cyrillic -> "ҚҚССИЗ";
                default -> "БЕЗ НДС";
            };
        }
    };

    private final String code;
    private final String name;

    CustomerVatTypeEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CustomerVatTypeEnum get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerVatTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static CustomerVatTypeEnum fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerVatTypeEnum");
    }
}
