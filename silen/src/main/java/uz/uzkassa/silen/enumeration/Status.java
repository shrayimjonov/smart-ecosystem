package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.EnumDTO;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
public enum Status {
    DRAFT("Сохраненные", "Сохранено", "sent"),
//    SENT("Отправленные", "Отправлено"),
//    QUEUED("В отправке", "В отправке"),
    PENDING("Ожидающие подписи", "Ожидает подписи", "sent"),
    CANCELLED("Отмененные", "Отменено", "cancel"),
    AGENT_ACCEPTED("Подписанные агентом", "Подписано агентом", "sent"),
    AGENT_REJECTED("Отклоненные агентом", "Отклонено агентом", "sent"),
    ACCEPTED("Подтвержденные", "Подтверждено", "accept"),
    REJECTED("Отклоненные", "Отклонено", "reject"),
    INVALID("Не действительный", "Не действительно", "invalid"),
    VALID("Действительный", "Действительно", "valid")/*,
    REVOKED("Измененные", "Изменено"),
    ERROR("Ошибка в отправлении", "Ошибка в отправлении")*/;

    private final String ruText;
    private final String shortText;
    private final String nicStatus;

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

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(Status.values().length);
        for (Status item : Status.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto(){
        return new EnumDTO(name(), getRuText());
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
}
