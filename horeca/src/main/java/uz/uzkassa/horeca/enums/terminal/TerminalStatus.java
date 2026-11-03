package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.horeca.dto.terminal.TerminalSelectItemDTO;

import java.util.ArrayList;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
@AllArgsConstructor
public enum TerminalStatus {
    CREATED("Создано", "Создано"),
    ON_SELL("В процессе продажи", "В процессе продажи"),
    SOLD("Продано", "Продано");

    private final String ruText;
    private final String shortText;

    public static List<TerminalSelectItemDTO> getAll() {
        List<TerminalSelectItemDTO> items = new ArrayList<>(TerminalStatus.values().length);
        for (TerminalStatus item : TerminalStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    public TerminalSelectItemDTO toDto() {
        return new TerminalSelectItemDTO(name(), getRuText());
    }

    public static TerminalStatus fromString(String code) {
        if (code != null) {
            for (TerminalStatus item : TerminalStatus.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    @JsonCreator
    public static TerminalStatus forValue(String value) {
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
