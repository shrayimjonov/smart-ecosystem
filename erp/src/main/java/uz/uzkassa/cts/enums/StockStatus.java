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
public enum StockStatus {
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
    CANCELLED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bekor qilingan";
                case uz_cyrillic -> "Бекор қилинган";
                default -> "Отменен";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bajarilgan";
                case uz_cyrillic -> "Бажарилган";
                default -> "Завершен";
            };
        }
    };

    private final String name;
    private final String code;

    StockStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static StockStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown StockStatus enum value: " + code));
    }

    @JsonCreator
    public static StockStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for StockStatus");
    }
}
