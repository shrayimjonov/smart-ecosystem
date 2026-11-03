package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Getter
@AllArgsConstructor
public enum TuronMarkStatus implements Serializable {
    RECEIVED("Получен"),
    EMITTED("Эмитирован"),
    APPLIED("Нанесен"),
    INTRODUCED("Введен в оборот"),
    WRITTEN_OFF("Утилизирован"),
    WITHDRAWN("Выведен из оборота"),
    RESERVED_NOT_USED("Зарезервировано. Не использовать"),
    INTRODUCED_RETURNED("Возвращен в оборот"),
    DISAGGREGATED("Дезагрегирован"),
    WAIT_SHIPMENT("Ожидает отгрузки"),
    EXPORTED("Используется для документов эспорта"),
    APPLIED_NOT_PAID("Нанесен, не оплачен"),
    ERROR("Ошибка"); //system status

    private final String text;

    public static TuronMarkStatus fromString(String code) {
        if (code != null) {
            for (TuronMarkStatus item : TuronMarkStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        return Arrays.stream(TuronMarkStatus.values())
                .map(TuronMarkStatus::toDto)
                .toList();
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.ordinal(), this.name(), this.getText());
    }

    @JsonCreator
    public static TuronMarkStatus forValue(String value) {
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

    public static boolean isSuccess(TuronMarkStatus status) {
        return EnumSet.of(TuronMarkStatus.APPLIED, TuronMarkStatus.INTRODUCED).contains(status);
    }
}
