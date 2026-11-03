package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.07.2023
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteCustomerStatisticsItemDTO {

    String dataType;

    Long total;
}
