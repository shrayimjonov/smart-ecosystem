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
public enum ThymeleafTemplateType implements Serializable {

    CONTRACT_FOR_BUY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sotib olish shartnomasi";
                case uz_cyrillic -> "Сотиб олиш шартномаси";
                default -> "Договор на покупку";
            };
        }
    },
    CONTRACT_FOR_SERVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xizmat ko'rsatish shartnomasi";
                case uz_cyrillic -> "Хизмат кўрсатиш шартномаси";
                default -> "Договор на оказание услуг";
            };
        }
    },
    CONTRACT_FOR_LEASING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Lizing shartnomasi";
                case uz_cyrillic -> "Лизинг шартномаси";
                default -> "Договор на лизинг";
            };
        }
    },
    CONTRACT_FOR_RENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ijara shartnomasi";
                case uz_cyrillic -> "Ижара шартномаси";
                default -> "Договор на аренду";
            };
        }
    },
    SPECIFICATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Spetsifikatsiya";
                case uz_cyrillic -> "Спецификация";
                default -> "Спецификация";
            };
        }
    },
    POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ishonchnoma";
                case uz_cyrillic -> "Ишончнома";
                default -> "Доверенность";
            };
        }
    },
    INVOICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hisob faktura";
                case uz_cyrillic -> "Ҳисоб фактура";
                default -> "Счет фактура";
            };
        }
    };

    private final String code;
    private final String name;

    ThymeleafTemplateType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ThymeleafTemplateType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ThymeleafTemplateTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static ThymeleafTemplateType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ThymeleafTemplateTypeEnum");
    }
}
