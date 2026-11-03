package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum WaybillDeliveryType {
    PICKUP("Со склада на склад", 1),
    DELIVERY("От продавца к покупателю", 2);

    private final String text;
    private final Integer code;

    public static WaybillDeliveryType fromString(String name) {
        if (name == null) return WaybillDeliveryType.DELIVERY;

        for (WaybillDeliveryType item : WaybillDeliveryType.values()) {
            if (name.equals(item.name())) {
                return item;
            }
        }
        return WaybillDeliveryType.DELIVERY;
    }

    public static List<EnumDTO> getAll() {
        return Arrays.stream(WaybillDeliveryType.values())
            .map(WaybillDeliveryType::toDto)
            .toList();
    }

    @JsonCreator
    public static WaybillDeliveryType fromCode(Integer code) {
        if (code == null) return WaybillDeliveryType.DELIVERY;

        for (WaybillDeliveryType item : WaybillDeliveryType.values()) {
            if (code.equals(item.getCode())) {
                return item;
            }
        }
        return WaybillDeliveryType.DELIVERY;
    }

    @JsonCreator
    public static WaybillDeliveryType forValue(String value) {
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
