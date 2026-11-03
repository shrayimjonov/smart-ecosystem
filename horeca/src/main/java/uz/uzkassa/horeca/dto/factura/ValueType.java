package uz.uzkassa.horeca.dto.factura;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
