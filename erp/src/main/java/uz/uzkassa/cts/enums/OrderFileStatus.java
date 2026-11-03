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
public enum OrderFileStatus {
    WAITING_FOR_DOCUMENT_ATTACHMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hujjat ilova qilinishi kutilmoqda";
                case uz_cyrillic -> "Ҳужжат илова қилиниши кутилмоқда";
                default -> "Ожидается прикрепление документа";
            };
        }
    },
    DOCUMENT_ATTACHED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hujjat ilova qilingan";
                case uz_cyrillic -> "Ҳужжат илова қилинган";
                default -> "Документ прикреплен";
            };
        }
    },
    ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlangan";
                case uz_cyrillic -> "Тасдиқланган";
                default -> "Принят";
            };
        }
    };

    private final String code;
    private final String name;

    OrderFileStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static OrderFileStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown OrderFileStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static OrderFileStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OrderFileStatusEnum");
    }
}
