package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum HallItemStatus implements Serializable {
    FREE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bo`sh";
                case cyrillic -> "Бўш";
                case en -> "Free";
                default -> "Свободный";
            };
        }
    },
    BOOKED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Band qilingan";
                case cyrillic -> "Банд қилинган";
                case en -> "Booked";
                default -> "Забронированный";
            };
        }
    },
    BLOCKED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bloklangan";
                case cyrillic -> "Блокланган";
                case en -> "Blocked";
                default -> "Заблокированный";
            };
        }
    },
    ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol";
                case cyrillic -> "Фаол";
                case en -> "Active";
                default -> "Активный";
            };
        }
    },
    IN_ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol";
                case cyrillic -> "Фаол";
                case en -> "Active";
                default -> "Активный";
            };
        }
    },
    UNITED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Birlashtirilgan";
                case cyrillic -> "Бирлаштирилган";
                case en -> "United";
                default -> "Объединенный";
            };
        }
    };

    private final String name;
    private final String code;

    HallItemStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }


    public static HallItemStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown HallItemStatus enum value: " + code));
    }

    @JsonCreator
    public static HallItemStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for HallItemStatus");
    }

    public static HallItemStatus getByCode(String code) {
        return Arrays.stream(HallItemStatus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
