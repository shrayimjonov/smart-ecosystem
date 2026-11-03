package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerBranchStatus implements Serializable {

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
    IN_ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol emas";
                case uz_cyrillic -> "Фаол эмас";
                default -> "Неактивный";
            };
        }
    },
    TEMPORARY_CLOSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Vaqtinchalik yopiq";
                case uz_cyrillic -> "Вақтинчалик ёпиқ";
                default -> "Временно закрыто";
            };
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko'rib chiqilmoqda";
                case uz_cyrillic -> "Кўриб чиқилмоқда";
                default -> "В ожидании";
            };
        }
    };

    private final String code;
    private final String name;

    CustomerBranchStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CustomerBranchStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerBranchStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static CustomerBranchStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerBranchStatusEnum");
    }
}
