package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApayMerchantReferenceType {
    SUPPLY("Supply"),
    SMARTPOS("Smartpos"),
    HORECA("Horeca");

    private final String name;
    private final String code;

    ApayMerchantReferenceType(String name) {
        this.code = this.name();
        this.name = name;
    }

    public static ApayMerchantReferenceType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ApayMerchantReferenceType enum value: " + code));
    }

    @JsonCreator
    public static ApayMerchantReferenceType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ApayMerchantReferenceType");
    }
}
