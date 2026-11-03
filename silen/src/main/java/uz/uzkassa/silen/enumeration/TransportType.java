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
public enum TransportType {
    Roadway("Грузовиком", 1),
    Airway("Самолетом", 2),
    Railway("По железной дороге", 3),
    Shipway("Сквозь воду", 4);

    private final String text;
    private final Integer code;

    public static TransportType fromString(String code) {
        if (code != null) {
            for (TransportType item : TransportType.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(TransportType.values().length);
        for (TransportType item : TransportType.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    @JsonCreator
    public static TransportType forValue(String value) {
        return fromString(value);
    }

    @JsonCreator
    public static TransportType fromCode(Integer code) {
        if (code != null) {
            for (TransportType item : TransportType.values()) {
                if (code.equals(item.getCode())) {
                    return item;
                }
            }
        }
        return null;
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.getCode(), this.name(), this.getText());
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
