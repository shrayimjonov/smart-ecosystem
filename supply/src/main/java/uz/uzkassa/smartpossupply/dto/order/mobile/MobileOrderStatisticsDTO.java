package uz.uzkassa.smartpossupply.dto.order.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.01.2023 10:30
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileOrderStatisticsDTO {

    Long orderTotalCountForToday;

    BigDecimal orderTotalAmountForToday;

    Long orderTotalCount;

    BigDecimal orderTotalAmount;

    Integer planRouteCompletedTotal;

    Integer planRouteTotal;
}
