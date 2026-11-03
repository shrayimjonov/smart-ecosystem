package uz.uzkassa.smartpos.integration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WidgetType {
    CHECK_BOX("CHECK_BOX"),
    INPUT("INPUT"),
    RADIO_BUTTON("RADIO_BUTTON"),
    DROP_DOWN("DROP_DOWN");
    private final String name;
}
