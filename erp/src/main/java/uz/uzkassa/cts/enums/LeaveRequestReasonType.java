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
public enum LeaveRequestReasonType implements Serializable {

    DAILY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kunlik";
                case uz_cyrillic -> "Кунлик";
                default -> "Ежедневный";
            };
        }
    },
    HOURLY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Soatlik";
                case uz_cyrillic -> "Соатлик";
                default -> "Почасовой";
            };
        }
    },
    DAILY_HOURLY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kunlik-soatlik";
                case uz_cyrillic -> "Кунлик-соатлик";
                default -> "Ежедневно-почасовой";
            };
        }
    };


    private final String name;
    private final String code;

    LeaveRequestReasonType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static LeaveRequestReasonType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown LeaveRequestReasonType enum value: " + code));
    }

    @JsonCreator
    public static LeaveRequestReasonType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for LeaveRequestReasonType");
    }
}
