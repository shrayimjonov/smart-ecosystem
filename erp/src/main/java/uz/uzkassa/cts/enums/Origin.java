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
public enum Origin {
    SELF_MADE(1) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "O‘zi ishlab chiqargan";
                case uz_cyrillic -> "Ўзи ишлаб чиқарган";
                default -> "Самопроизводство";
            };
        }
    },
    PURCHASED(2) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sotib olingan";
                case uz_cyrillic -> "Сотиб олинган";
                default -> "Куплено";
            };
        }
    },
    SERVICE(3) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xizmat ko'rsatish";
                case uz_cyrillic -> "Хизмат кўрсатиш";
                default -> "Услуга";
            };
        }
    },
    NON_PARTICIPATION(4) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ishtirok etmayman";
                case uz_cyrillic -> "Иштирок этмайман";
                default -> "Не участвую";
            };
        }
    };

    private final String code;
    private final String name;
    private final Integer id;

    Origin(Integer id) {
        this.code = this.name();
        this.name = this.getName();
        this.id = id;
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static Origin get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown OriginEnum enum value: " + code));
    }

    @JsonCreator
    public static Origin fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OriginEnum");
    }
}
