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
public enum PurchaseOrderType {
    SELF("Silen"),
    OC("1c"),
    SUPPLY("Supply");

    private final String text;

    public static PurchaseOrderType fromString(String code) {
        if (code != null) {
            for (PurchaseOrderType item : PurchaseOrderType.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(PurchaseOrderType.values().length);
        for (PurchaseOrderType item : PurchaseOrderType.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.name(), this.getText());
    }

    @JsonCreator
    public static PurchaseOrderType forValue(String value) {
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
