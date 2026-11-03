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
public enum StockStatus {
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                case en -> "Draft";
                default -> "Черновик";
            };
        }
    },
    CANCELLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bekor qilingan";
                case cyrillic -> "Бекор қилинган";
                case en -> "Cancelled";
                default -> "Отменен";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bajarilgan";
                case cyrillic -> "Бажарилган";
                case en -> "Completed";
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

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
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

    public static StockStatus getByCode(String code) {
        return Arrays.stream(StockStatus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
