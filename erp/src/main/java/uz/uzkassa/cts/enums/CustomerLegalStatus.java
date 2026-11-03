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
public enum CustomerLegalStatus {
    ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol";
                case uz_cyrillic -> "Фаол";
                default -> "Активный";
            };
        }
    },
    SENT_TO_COURT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sudga topshirildi (Prime)";
                case uz_cyrillic -> "Судга топширилди (Prime)";
                default -> "Передан в суд (Prime)";
            };
        }
    },
    SENT_TO_COURT_CTS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sudga topshirildi (CTS)";
                case uz_cyrillic -> "Судга топширилди (CTS)";
                default -> "Передан в суд (CTS)";
            };
        }
    },
    CLAIM_PERIOD {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Da'vo muddati";
                case uz_cyrillic -> "Даъво муддати";
                default -> "Претензионный период";
            };
        }
    },
    BANKRUPT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bankrot";
                case uz_cyrillic -> "Банкрот";
                default -> "Банкрот";
            };
        }
    },
    WRIT_OF_EXECUTION_RECEIVED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ijro hujjati qabul qilindi";
                case uz_cyrillic -> "Ижро ҳужжати қабул қилинди";
                default -> "Исполнительный лист получен";
            };
        }
    };

    private final String code;
    private final String name;

    CustomerLegalStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CustomerLegalStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerLegalStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static CustomerLegalStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerLegalStatusEnum");
    }
}
