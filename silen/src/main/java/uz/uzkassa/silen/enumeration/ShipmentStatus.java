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
public enum ShipmentStatus {
    NEW("Новая"),
    IN_PROGRESS("Отгружается"),
    CLOSED("Отгружено"),
    CANCELLED("Отменено");

    private final String text;

    public static ShipmentStatus fromString(String code) {
        if (code != null) {
            for (ShipmentStatus item : ShipmentStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(ShipmentStatus.values().length);
        for (ShipmentStatus item : ShipmentStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.name(), this.getText());
    }

    @JsonCreator
    public static ShipmentStatus forValue(String value) {
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
