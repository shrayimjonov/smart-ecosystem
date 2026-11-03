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
public enum OrderStatus {

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
    WAITING_FOR_SPECIFICATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SPETSIFICATSIYA ILOVA QILINISHI KUTILMOQDA";
                case uz_cyrillic -> "СПЕЦИФИКАЦИЯ ИЛОВА ҚИЛИНИШИ КУТИЛМОКДА";
                default -> "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ СПЕЦИФИКАЦИИ";
            };
        }
    },
    SPECIFICATION_ATTACHED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SPETSIFICATSIYA ILOVA QILINGAN";
                case uz_cyrillic -> "СПЕЦИФИКАЦИЯ ИЛОВА ҚИЛИНГАН";
                default -> "СПЕЦИФИКАЦИЯ ПРИКРЕПЛЕНA";
            };
        }
    },
    WAITING_FOR_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "ISHONCHNOMA ILOVA QILINISHI KUTILMOQDA";
                case uz_cyrillic -> "ИШОНЧНОМА ИЛОВА ҚИЛИНИШИ КУТИЛМОКДА";
                default -> "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ ДОВЕРНОСТИ";
            };
        }
    },
    WAITING_FOR_CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SHARTNOMA ILOVA QILINISHI KUTILMOQDA";
                case uz_cyrillic -> "ШАРТНОМА ИЛОВА ҚИЛИНИШИ КУТИЛМОКДА";
                default -> "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ ДОГОВОРА";
            };
        }
    },
    WAITING_FOR_INVOICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HISOB FAKTURA ILOVA QILINISHI KUTILMOQDA";
                case uz_cyrillic -> "ҲИСОБ ФАКТУРА ИЛОВА ҚИЛИНИШИ КУТИЛМОКДА";
                default -> "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ СЧЕТ ФАКТУРИ";
            };
        }
    },
    DOCUMENT_CREATED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUJJAT YARATILDI";
                case uz_cyrillic -> "ҲУЖЖAТ ЯРAТИЛДИ";
                default -> "ДОКУМЕНТ СОЗДАН";
            };
        }
    },
    WAITING_FOR_DOCUMENT_ATTACHMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HUJJAT ILOVA QILINISHI KUTILMOQDA";
                case uz_cyrillic -> "ҲУЖЖAТ ИЛОВА ҚИЛИНИШИ КУТИЛМОКДА";
                default -> "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ ДОКУМЕНТА";
            };
        }
    },
    WAITING_FOR_PAYMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TO'LOVNI KUTMOQDA";
                case uz_cyrillic -> "ТЎЛОВНИ КУТМОҚДA";
                default -> "В ОЖИДАНИИ ОПЛАТЫ";
            };
        }
    },
    PAYMENT_ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TO'LOV QABUL QILINDI";
                case uz_cyrillic -> "ТЎЛОВ ҚAБУЛ ҚИЛИНДИ";
                default -> "ПЛАТЕЖ ПРИНЯТ";
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

    OrderStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static OrderStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus enum value: " + code));
    }

    @JsonCreator
    public static OrderStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OrderStatus");
    }
}
