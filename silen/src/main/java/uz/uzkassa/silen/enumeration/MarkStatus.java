package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Getter
@AllArgsConstructor
public enum MarkStatus {
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
    ERROR("Ошибка");//system status

    private final String text;

    public static MarkStatus fromString(String code) {
        if (code != null) {
            for (MarkStatus item : MarkStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(MarkStatus.values().length);
        for (MarkStatus item : MarkStatus.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    @JsonCreator
    public static MarkStatus forValue(String value) {
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

    public static boolean checkStatus(MarkStatus status) {
        return EnumSet.of(DISAGGREGATED, WRITTEN_OFF).contains(status);
    }
}
