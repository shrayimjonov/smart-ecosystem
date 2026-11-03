package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.smartpos.dto.EnumDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ShipmentStatus {
    NEW("Новая"),
    IN_PROGRESS("Отгружается"),
    CLOSED("Отгружено");

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
    public static ShipmentStatus forValue(Map<String, String> value) {
        return fromString(value.get("text"));
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
