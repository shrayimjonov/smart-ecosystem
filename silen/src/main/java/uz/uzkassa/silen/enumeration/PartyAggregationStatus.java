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
public enum PartyAggregationStatus {
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    UTILISED("Нанесен"),
    ERROR("Ошибка"),
    CLOSED("Аггрегировано");

    private final String text;

    public static PartyAggregationStatus fromString(String code) {
        if (code != null) {
            for (PartyAggregationStatus item : PartyAggregationStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return NEW;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(PartyAggregationStatus.values().length);
        for (PartyAggregationStatus item : PartyAggregationStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.name(), this.getText());
    }

    @JsonCreator
    public static PartyAggregationStatus forValue(String value) {
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
