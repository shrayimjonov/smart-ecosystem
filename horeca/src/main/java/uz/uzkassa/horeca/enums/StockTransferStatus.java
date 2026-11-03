package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.Arrays;
import java.util.Map;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum StockTransferStatus {
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    },
    IN_TRANSIT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yo`lda";
                case cyrillic -> "Йўлда";
                default -> "Транзит";
            };
        }
    },

    APPROVED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tasdiqlangan";
                case cyrillic -> "Тасдиқланган";
                default -> "Одобрено";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rad etilgan";
                case cyrillic -> "Рад этилган";
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

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
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

    public static StockTransferStatus getByCode(String code) {
        return Arrays.stream(StockTransferStatus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
