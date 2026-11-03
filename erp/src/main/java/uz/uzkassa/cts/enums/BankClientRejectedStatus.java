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
public enum BankClientRejectedStatus {
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "KUTILMOQDA";
                case uz_cyrillic -> "КУТИЛМОКДА";
                default -> "В ОЖИДАНИИ";
            };
        }
    },
    ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TASDIQLANGAN";
                case uz_cyrillic -> "ТАСДИКЛАНГАН";
                default -> "ПРИНЯТ";
            };
        }
    },
    HAS_BEEN_ADDED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "QO'SHILGAN";
                case uz_cyrillic -> "ҚЎШИЛГАН";
                default -> "ДОБАВЛЕН";
            };
        }
    },
    IN_BLACK_LIST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "QORA RO'YXATDA";
                case uz_cyrillic -> "ҚОРА РЎЙХАТДА";
                default -> "В ЧЕРНОМ СПИСКЕ";
            };
        }
    },
    EXPIRED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "MUDDATI O`TGAN";
                case uz_cyrillic -> "МУДДАТИ УТГАН";
                default -> "ПРОСРОЧЕННЫЙ";
            };
        }
    };

    private final String code;
    private final String name;

    BankClientRejectedStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static BankClientRejectedStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown BankClientRejectedStatus enum value: " + code));
    }

    @JsonCreator
    public static BankClientRejectedStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for BankClientRejectedStatus");
    }
}
