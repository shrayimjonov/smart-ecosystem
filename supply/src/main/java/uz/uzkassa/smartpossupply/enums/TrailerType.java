package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;

import java.util.ArrayList;
import java.util.List;


@Getter
@AllArgsConstructor
public enum TrailerType {
    TRAILER("Прицеп", 1),
    SEMITRAILER("Полуприцеп", 2);

    private final String text;
    private final Integer code;

    public static TrailerType fromString(String code) {
        if (code != null) {
            for (TrailerType item : TrailerType.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(TrailerType.values().length);
        for (TrailerType item : TrailerType.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.getCode(), this.name(), this.getText());
    }

    @JsonCreator
    public static TrailerType forValue(String value) {
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
