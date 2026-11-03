package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.horeca.dto.terminal.TerminalEnumDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
@AllArgsConstructor
public enum TerminalProductOrigin {
    SELF_MADE(1, "Производитель", "O'zi ishlab chiqargan"),
    IMPORT(2, "Куплено", "Sotib olingan"),
    SERVICE(3, "Услуга", "Xizmat kursatish"),
    NEUTRAL(4, "Не участвую", "Ishtirok etmayman");

    private final int code;
    private final String ruText;
    private final String uzText;

    public String getName() {
        return this.getRuText();
    }

    public static TerminalProductOrigin fromCode(int code) {
        for (TerminalProductOrigin item : TerminalProductOrigin.values()) {
            if (code == item.getCode()) {
                return item;
            }
        }
        return NEUTRAL;
    }

    public static TerminalProductOrigin fromString(String code) {
        if (code != null) {
            for (TerminalProductOrigin item : TerminalProductOrigin.values()) {
                if (code.equalsIgnoreCase(item.getRuText()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<TerminalEnumDTO> getAll() {
        return Arrays
            .stream(TerminalProductOrigin.values())
            .map(TerminalProductOrigin::toDto)
            .collect(Collectors.toCollection(() -> new ArrayList<>(TerminalProductOrigin.values().length)));
    }

    public TerminalEnumDTO toDto() {
        return new TerminalEnumDTO(getCode(), name(), getName());
    }

    @JsonCreator
    public static TerminalProductOrigin forCodeValue(String value) {
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
