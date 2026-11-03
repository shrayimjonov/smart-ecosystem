package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.11.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SystemUpgradeFilter extends BaseFilter {

    Boolean available;

    String platformType;
}
