package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TripStatus {
    NEW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangi";
                case cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    IN_PROGRESS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Jarayonda";
                case cyrillic -> "Жараёнда";
                default -> "В процессе";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bajarildi";
                case cyrillic -> "Бажарилди";
                default -> "Завершено";
            };
        }
    };

    private final String name;
    private final String code;

    TripStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public static TripStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown TripStatus enum value: " + code));
    }

    @JsonCreator
    public static TripStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for TripStatus");
    }
}
