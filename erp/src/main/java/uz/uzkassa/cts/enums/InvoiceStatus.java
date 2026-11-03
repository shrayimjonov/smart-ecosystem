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
public enum InvoiceStatus {

    CREATED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "YARATILDI";
                case uz_cyrillic -> "ЯРАТИЛДИ";
                default -> "СОЗДАНО";
            };
        }
    },
    DOCUMENT_ATTACHED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUJJAT ILOVA QILINGAN";
                case uz_cyrillic -> "ҲУЖЖAТ ИЛОВA ҚИЛИНГAН";
                default -> "ДОКУМЕНТ ПРИКРЕПЛЁН";
            };
        }
    },
    WAITING_FOR_DOCUMENT_ATTACHMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUJJAT ILOVA QILINISHI KUTILMOQDA";
                case uz_cyrillic -> "ҲУЖЖAТ ИЛОВA ҚИЛИНИСҲИ КУТИЛМОҚДA";
                default -> "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ ДОКУМЕНТА";
            };
        }
    },
    ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TASDIQLANGAN";
                case uz_cyrillic -> "ТAСДИҚЛAНГAН";
                default -> "ПРИНЯТ";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "YOPILGAN";
                case uz_cyrillic -> "ЁПИЛГАН";
                default -> "ЗАКРЫТО";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RAD QILINGAN";
                case uz_cyrillic -> "РAД ҚИЛИНГAН";
                default -> "ОТКЛОНЕН";
            };
        }
    };

    private final String code;
    private final String name;

    InvoiceStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static InvoiceStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown InvoiceStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static InvoiceStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for InvoiceStatusEnum");
    }
}
