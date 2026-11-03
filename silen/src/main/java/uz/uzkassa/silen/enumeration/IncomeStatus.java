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
public enum IncomeStatus {
    NEW("Новая"),
    COMPLETED("Оприходовано");

    private String text;

    public static IncomeStatus fromString(String code) {
        if (code != null) {
            for (IncomeStatus item : IncomeStatus.values()) {
                if (code.equalsIgnoreCase(item.getText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<EnumDTO> getAll() {
        List<EnumDTO> items = new ArrayList<>(IncomeStatus.values().length);
        for (IncomeStatus item : IncomeStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public EnumDTO toDto() {
        return new EnumDTO(this.name(), this.getText());
    }

    @JsonCreator
    public static IncomeStatus forValue(String value) {
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
