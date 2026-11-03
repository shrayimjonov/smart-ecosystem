package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.smartpos.dto.SelectItemDTO;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
public enum Status {
    DRAFT("Сохраненные", "Сохраненные"),
    PENDING("Ожидающие подписи", "Ожидает подписи"),
    CANCELLED("Отмененные", "Отменено"),
    AGENT_ACCEPTED("Подписанные агентом", "Подписано агентом"),
    AGENT_REJECTED("Отклоненные агентом", "Отклонено агентом"),
    ACCEPTED("Подтвержденные", "Подтверждено"),
    REJECTED("Отклоненные", "Отклонено"),
    INVALID("Не действительный", "Не действительно"),
    VALID("Действительный", "Действительно")/*,
    REVOKED("Измененные", "Изменено"),
    ERROR("Ошибка в отправлении", "Ошибка в отправлении")*/;

    private final String ruText;
    private final String shortText;

    public static List<SelectItemDTO> getAll() {
        List<SelectItemDTO> items = new ArrayList<>(Status.values().length);
        for (Status item : Status.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public SelectItemDTO toDto() {
        return new SelectItemDTO(name(), getRuText());
    }

    public static Status fromString(String code) {
        if (code != null) {
            for (Status item : Status.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
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
        return switch (this) {
            case PENDING -> "sent";
            case ACCEPTED -> "accept";
            case REJECTED -> "reject";
            case CANCELLED -> "cancel";
            default -> null;
        };
    }
}
