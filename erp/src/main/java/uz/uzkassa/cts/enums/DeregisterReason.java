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
public enum DeregisterReason implements Serializable {

    LIQUIDATED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tadbirkorlik subyekti to'qtatildi";
                case uz_cyrillic -> "Тадбиркорлик субъекти тугатилди";
                default -> "Юридическое лицо было приостановлено";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Onlayn-NKM xizmatidan voz kechildi";
                case uz_cyrillic -> "Онлайн-НКМ хизматидан воз кечилди";
                default -> "Сервис Онлайн-ККМ прекращен";
            };
        }
    };

    private final String code;
    private final String name;

    DeregisterReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DeregisterReason get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown DeregisterReasonEnum enum value: " + code));
    }

    @JsonCreator
    public static DeregisterReason fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DeregisterReasonEnum");
    }
}
