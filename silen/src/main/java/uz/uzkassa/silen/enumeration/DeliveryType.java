package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.EnumDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum DeliveryType {
    PICKUP("со склада на склад", 1),
    DELIVERY("от продавца к покупателю", 2);

    private final String text;
    private final Integer code;

    public static DeliveryType fromString(String name) {
        if (name == null) return DeliveryType.DELIVERY;

        for (DeliveryType item : DeliveryType.values()) {
            if (name.equals(item.name())) {
                return item;
            }
        }
        return DeliveryType.DELIVERY;
    }

    public static List<EnumDTO> getAll() {
        return Arrays.stream(DeliveryType.values())
            .map(DeliveryType::toDto)
            .collect(Collectors.toList());
    }

    @JsonCreator
    public static DeliveryType fromCode(Integer code) {
        if (code == null) return DeliveryType.DELIVERY;

        for (DeliveryType item : DeliveryType.values()) {
            if (code.equals(item.getCode())) {
                return item;
            }
        }
        return DeliveryType.DELIVERY;
    }

    @JsonCreator
    public static DeliveryType forValue(String value) {
        return fromString(value);
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
