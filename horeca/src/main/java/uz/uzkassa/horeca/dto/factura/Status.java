package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Status {
    DRAFT("Черновик"),
    SENT("Отправленные"),
    QUEUED("В отправке"),
    PENDING("Ожидающие"),
    CANCELLED("Отмененные"),
    AGENT_ACCEPTED("Подписанные агентом"),
    AGENT_REJECTED("Отклоненные агентом"),
    ACCEPTED("Подписанные"),
    REJECTED("Отказанные"),
    REVOKED("Измененные"),
    ERROR("Ошибка в отправлении");

    private final String name;

    public static Status fromString(String code) {
        if (code != null) {
            for (Status item : Status.values()) {
                if (code.equalsIgnoreCase(item.getName()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    @JsonCreator
    public static Status forValue(String value) {
        return fromString(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }

    public String getFacturaStatus() {
        switch (this) {
            case PENDING:
                return "sent";
            case ACCEPTED:
                return "accept";
            case REJECTED:
                return "reject";
            case CANCELLED:
                return "cancel";
        }
        return null;
    }
}
