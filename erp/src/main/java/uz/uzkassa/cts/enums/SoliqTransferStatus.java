package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SoliqTransferStatus implements Serializable {

    TRANSFERRED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Передан в НИЦ";
                case uz_cyrillic -> "Передан в НИЦ";
                default -> "Передан в НИЦ";
            };
        }
    },
    NOT_TRANSFERRED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Не передан в НИЦ";
                case uz_cyrillic -> "Не передан в НИЦ";
                default -> "Не передан в НИЦ";
            };
        }
    };

    private final String code;
    private final String name;

    SoliqTransferStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static SoliqTransferStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown SoliqTransferStatus enum value: " + code));
    }

    @JsonCreator
    public static SoliqTransferStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for SoliqTransferStatus");
    }

    public static SoliqTransferStatus fromRussianToSoliqTransferStatusEnum(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return switch (value.trim()) {
            case "Передан в НИЦ" -> TRANSFERRED;
            case "Не передан в НИЦ" -> NOT_TRANSFERRED;
            default -> throw new IllegalArgumentException("Unknown Russian SoliqTransferStatus value: " + value);
        };
    }
}
