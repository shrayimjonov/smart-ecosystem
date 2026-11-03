package uz.uzkassa.horeca.dto.factura;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.09.2023
 */

@Getter
@AllArgsConstructor
public enum WidgetType {
    CHECK_BOX("CHECK_BOX"),
    INPUT("INPUT"),
    RADIO_BUTTON("RADIO_BUTTON"),
    DROP_DOWN("DROP_DOWN");

    private final String name;
}
