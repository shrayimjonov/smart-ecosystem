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
public enum WaybillLocalType {
    STANDART("Стандартный", 0),
    ADDITIONAL("Дополнительный", 1),
    EDITED("Исправленный", 4);

    private final String text;
    private final Integer code;

    public static WaybillLocalType fromString(String code) {
        if (code != null) {
            for (WaybillLocalType item : WaybillLocalType.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return STANDART;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(WaybillLocalType.values().length);
        for (WaybillLocalType item : WaybillLocalType.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    @JsonCreator
    public static WaybillLocalType forValue(String value) {
        return fromString(value);
    }

    @JsonCreator
    public static WaybillLocalType fromCode(Integer code) {
        if (code != null) {
            for (WaybillLocalType item : WaybillLocalType.values()) {
                if (code.equals(item.getCode())) {
                    return item;
                }
            }
        }
        return STANDART;
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
