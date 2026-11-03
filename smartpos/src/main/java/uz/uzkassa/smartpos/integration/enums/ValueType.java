package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.09.2023
 */

@Getter
@AllArgsConstructor
public enum ValueType {
    NUMBER("NUMBER"),
    BOOLEAN("BOOLEAN"),
    TEXT("TEXT");
    private final String name;

    @JsonCreator
    public static ValueType forValue(Map<String, String> map) {
        return ValueType.valueOf(map.get("code"));
    }
}
