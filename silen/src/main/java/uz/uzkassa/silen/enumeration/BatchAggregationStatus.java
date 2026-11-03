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
public enum BatchAggregationStatus {
    NEW("Новая"),
    PENDING("В процессе"),
    COMPLETED("Завершено"),
    INVALID("Не действительный");

    private final String ruText;

    public static BatchAggregationStatus fromString(String code) {
        if (code != null) {
            for (BatchAggregationStatus item : BatchAggregationStatus.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(BatchAggregationStatus.values().length);
        for (BatchAggregationStatus item : BatchAggregationStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    @JsonCreator
    public static BatchAggregationStatus forValue(String value) {
        return fromString(value);
    }

    public EnumDTO toDto() {
        return new EnumDTO(name(), getRuText());
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
