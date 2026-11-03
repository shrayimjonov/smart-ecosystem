package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.EnumDTO;

import java.util.*;

@Getter
@AllArgsConstructor
public enum PurchaseOrderStatus {
    NEW("Новый"),
    APPROVED("Одобрено"),
    REJECTED("Отклонено"),
    IN_PROGRESS("В процессе"),
    CLOSED("Завершен");

    private final String text;

    public static PurchaseOrderStatus fromString(String code) {
        if (code != null) {
            for (PurchaseOrderStatus item : PurchaseOrderStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(PurchaseOrderStatus.values().length);
        for (PurchaseOrderStatus item : PurchaseOrderStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public boolean checkingForUpdateStatus(PurchaseOrderStatus status) {
        Map<PurchaseOrderStatus, EnumSet<PurchaseOrderStatus>> map = new HashMap<>();
        map.put(NEW, EnumSet.of(APPROVED, REJECTED));
        map.put(APPROVED, EnumSet.of(IN_PROGRESS));
        map.put(IN_PROGRESS, EnumSet.of(CLOSED));

        if (map.containsKey(this))
            return map.get(this).contains(status);
        else
            return false;
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.name(), this.getText());
    }

    @JsonCreator
    public static PurchaseOrderStatus forValue(String value) {
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
