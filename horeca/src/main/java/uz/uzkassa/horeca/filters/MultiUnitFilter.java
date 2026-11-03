package uz.uzkassa.horeca.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.05.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultiUnitFilter extends BaseFilter {

    Long menuItemId;

    Long techCardId;
}
