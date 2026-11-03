package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MailingListItemStatus {

    SENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuborilgan";
                case cyrillic -> "Юборилган";
                default -> "Отправлено";
            };
        }
    },
    FAILED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Muvaffaqiyatsiz";
                case cyrillic -> "Муваффақиясиз";
                default -> "Неуспешно";
            };
        }
    },
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Jarayonda";
                case cyrillic -> "Жараёнда";
                default -> "В ожидании";
            };
        }
    };

    private final String code;
    private final String name;

    MailingListItemStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static MailingListItemStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown MailingListItemStatus enum value: " + code));
    }

    @JsonCreator
    public static MailingListItemStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for MailingListItemStatus");
    }
}
