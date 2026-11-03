package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum StockTransferStatus {
    DRAFT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qoralama";
                case uz_cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    },
    IN_TRANSIT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yo`lda";
                case uz_cyrillic -> "Йўлда";
                default -> "Транзит";
            };
        }
    },

    APPROVED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlangan";
                case uz_cyrillic -> "Тасдиқланган";
                default -> "Одобрено";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad etilgan";
                case uz_cyrillic -> "Рад этилган";
                default -> "Отклонено";
            };
        }
    };

    private final String name;
    private final String code;

    StockTransferStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static StockTransferStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown StockTransferStatus enum value: " + code));
    }

    @JsonCreator
    public static StockTransferStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for StockTransferStatus");
    }
}
