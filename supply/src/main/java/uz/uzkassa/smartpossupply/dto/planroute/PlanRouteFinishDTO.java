package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PlanRouteReason;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date 30.05.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteFinishDTO {

    Long planRouteCustomerBranchId;

    Double longitude;

    Double latitude;

    String description;

    PlanRouteReason reason;
}
