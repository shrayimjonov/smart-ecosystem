package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.12.2022 17:39
 */
@Getter
@Setter
@NoArgsConstructor
public class ShiftReportFilter extends BaseFilter {

    Long cashierId;

    Long shiftNumber;
}
