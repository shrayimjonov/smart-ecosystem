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
public enum ProductStatus {
    PENDING("В ожидании"),
    ACCEPTED("Подтвержденный"),
    REJECTED("Отклоненный");

    private String text;

    public static ProductStatus fromString(String code) {
        if (code != null) {
            for (ProductStatus item : ProductStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(ProductStatus.values().length);
        for (ProductStatus item : ProductStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto(){
        return new EnumDTO(name(), getText());
    }

    @JsonCreator
    public static ProductStatus forValue(String value) {
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
