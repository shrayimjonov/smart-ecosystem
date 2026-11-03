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
public enum CandidateStatus {
    NEW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi";
                case uz_cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    INTERVIEW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Suhbat";
                case uz_cyrillic -> "Сухбат";
                default -> "Собеседование";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad etilgan";
                case uz_cyrillic -> "Рад этилган";
                default -> "Отказано";
            };
        }
    },
    OFFER_PROVIDED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Taklif";
                case uz_cyrillic -> "Таклиф";
                default -> "Предложение";
            };
        }
    },
    OFFER_REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Taklifdan rad etilgan";
                case uz_cyrillic -> "Таклифдан рад этилган";
                default -> "Отказ от предложения";
            };
        }
    },
    ACCEPTED_OFFER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qabul qilingan";
                case uz_cyrillic -> "Кабул қилинган";
                default -> "Принято";
            };
        }
    };

    private final String code;
    private final String name;

    CandidateStatus() {
        this.code = name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CandidateStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown CandidateStatus enum value: " + code));
    }

    @JsonCreator
    public static CandidateStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CandidateStatus");
    }
}
