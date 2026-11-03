package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum CaseStatus implements Serializable {

    NEW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi";
                case uz_cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko`rilmoqda";
                case uz_cyrillic -> "Кўрилмоқда";
                default -> "В ожидании";
            };
        }
    },
    DEFECTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Defekt";
                case uz_cyrillic -> "Дефект";
                default -> "Дефект";
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
    },
    ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlandi";
                case uz_cyrillic -> "Тасдикланди";
                default -> "Принято";
            };
        }
    },
    DRAFT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qoralama";
                case uz_cyrillic -> "Коралама";
                default -> "Черновик";
            };
        }
    },
    FIRST_STEP_FINISHED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Birinchi qadam tugatildi";
                case uz_cyrillic -> "Биринчи кадам тугатилди";
                default -> "Первый шаг завершен";
            };
        }
    };

    private final String code;
    private final String name;

    CaseStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CaseStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown CaseStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static CaseStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CaseStatusEnum");
    }
}
