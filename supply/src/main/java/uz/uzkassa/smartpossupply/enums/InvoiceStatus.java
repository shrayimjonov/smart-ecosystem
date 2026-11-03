package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum InvoiceStatus implements Serializable {
    DRAFT(Status.DRAFT) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    },
    PENDING(Status.PENDING) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kutilmoqda";
                case cyrillic -> "Кутилмоқда";
                default -> "Ожидающие";
            };
        }
    },
    CANCELLED(Status.CANCELLED) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bekor qilingan";
                case cyrillic -> "Бекор қилинган";
                default -> "Отмененные";
            };
        }
    },
    AGENT_ACCEPTED(Status.AGENT_ACCEPTED) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agent tomonidan qabul qilingan";
                case cyrillic -> "Агент томонидан қабул қилинган";
                default -> "Подписанные агентом";
            };
        }
    },
    AGENT_REJECTED(Status.AGENT_REJECTED) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agent tomonidan rad etilgan";
                case cyrillic -> "Агент томонидан рад этилган";
                default -> "Отклоненные агентом";
            };
        }
    },
    ACCEPTED(Status.ACCEPTED) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Imzolangan";
                case cyrillic -> "Имзоланган";
                default -> "Подписанные";
            };
        }
    },
    REJECTED(Status.REJECTED) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rad etilgan";
                case cyrillic -> "Рад этилган";
                default -> "Отказанные";
            };
        }
    }
    ;

    private final String name;
    private final String code;
    private final Status facturaStatus;

    InvoiceStatus(Status facturaStatus) {
        this.code = this.name();
        this.name = this.getName();
        this.facturaStatus = facturaStatus;
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static InvoiceStatus forValue(Map<String, String> value) {
        return InvoiceStatus.valueOf(value.get("code"));
    }

    public static InvoiceStatus getByFacturaStatus(Status facturaStatus) {
        for (InvoiceStatus invoiceStatus : values()) {
            if (facturaStatus.equals(invoiceStatus.getFacturaStatus())) {
                return invoiceStatus;
            }
        }
        return InvoiceStatus.PENDING;
    }
}
