package uz.uzkassa.smartpossupply.enums;

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
    CHECK_BOX_GROUP("CHECK_BOX_GROUP"),
    INPUT("INPUT"),
    RADIO_BUTTON("RADIO_BUTTON"),
    DROP_DOWN("DROP_DOWN"),
    CALENDAR("CALENDAR"),
    SELECT("SELECT");
    private final String name;
}
