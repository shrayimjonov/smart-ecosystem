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
public enum VacancyStatus {
    DRAFT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Draft";
                case uz_cyrillic -> "Draft";
                default -> "Draft";
            };
        }
    },
    OPEN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Open";
                case uz_cyrillic -> "Open";
                default -> "Open";
            };
        }
    },
    IN_PROGRESS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "In Progress";
                case uz_cyrillic -> "In Progress";
                default -> "In Progress";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Closed";
                case uz_cyrillic -> "Closed";
                default -> "Closed";
            };
        }
    },
    CANCELLED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Cancelled";
                case uz_cyrillic -> "Cancelled";
                default -> "Cancelled";
            };
        }
    };

    private final String code;
    private final String name;

    VacancyStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static VacancyStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown VacancyStatus enum value: " + code));
    }

    @JsonCreator
    public static VacancyStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for VacancyStatus");
    }
}
