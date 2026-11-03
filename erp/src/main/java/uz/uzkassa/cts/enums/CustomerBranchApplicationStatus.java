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
public enum CustomerBranchApplicationStatus implements Serializable {
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko'rib chiqilmoqda";
                case uz_cyrillic -> "Кўриб чиқилмоқда";
                default -> "В ожидании";
            };
        }
    },
    PARTIALLY_ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qisman qabul qilingan";
                case uz_cyrillic -> "Қисман қабул қилинган";
                default -> "Частично принято";
            };
        }
    },
    ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qabul qilingan";
                case uz_cyrillic -> "Қабул қилинган";
                default -> "Принято";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad etildi";
                case uz_cyrillic -> "Рад этилди";
                default -> "Отказано";
            };
        }
    };

    private final String name;
    private final String code;

    CustomerBranchApplicationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CustomerBranchApplicationStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerBranchApplicationStatus enum value: " + code));
    }

    @JsonCreator
    public static CustomerBranchApplicationStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerBranchApplicationStatus");
    }
}
