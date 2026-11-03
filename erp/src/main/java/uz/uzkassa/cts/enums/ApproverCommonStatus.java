package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApproverCommonStatus {

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
    WAITING_FOR_APPROVAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlash kutilmoqda";
                case uz_cyrillic -> "Тасдиқлаш кутилмоқда";
                default -> "Ожидание одобрения";
            };
        }
    },
    APPROVED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlandi";
                case uz_cyrillic -> "Тасдиқланди";
                default -> "Одобрено";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad etildi";
                case uz_cyrillic -> "Рад этилди";
                default -> "Отклонено";
            };
        }
    };

    private final String code;
    private final String name;

    ApproverCommonStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ApproverCommonStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ApproverCommonStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static ApproverCommonStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ApproverCommonStatusEnum");
    }
}
