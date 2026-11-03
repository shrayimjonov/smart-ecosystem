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
public enum ApproveAction {
    SEND_TO_NEXT_APPROVER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Keyingi tasdiqlovchiga yuborish";
                case uz_cyrillic -> "Кейинги тасдиқловчига юбориш";
                default -> "Отправить следующему утверждающему";
            };
        }
    },
    MARK_AS_APPROVED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlash";
                case uz_cyrillic -> "Тасдиқлаш";
                default -> "Утвердить";
            };
        }
    },
    SEND_TO_PREVIOUS_APPROVER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Oldingi tasdiqlovchiga yuborish";
                case uz_cyrillic -> "Олдинги тасдиқловчига юбориш";
                default -> "Отправить предыдущему утверждающему";
            };
        }
    },
    SEND_TO_CREATOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yaratguvchiga yuborish";
                case uz_cyrillic -> "Яратгувчига юбориш";
                default -> "Отправить создателю";
            };
        }
    },
    MARK_AS_REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad qilish";
                case uz_cyrillic -> "Рад қилиш";
                default -> "Отклонять";
            };
        }
    };

    private final String name;
    private final String code;

    ApproveAction() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ApproveAction get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ApproveAction enum value: " + code));
    }

    @JsonCreator
    public static ApproveAction fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ApproveAction");
    }
}
