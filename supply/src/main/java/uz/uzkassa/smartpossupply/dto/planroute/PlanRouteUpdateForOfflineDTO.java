package uz.uzkassa.smartpossupply.dto.planroute;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteReason;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteUpdateForOfflineDTO {

    LocalDate visitPlanDate;
    Long planRouteCustomerBranchId;

    Long planRouteItemId;
    PlanRouteCustomerBranchStatus status;
    LocalDateTime startDate;
    LocalDateTime endDate;
    PlanRouteReason reason;

    boolean deleted;
    Long planRouteId;

    Boolean addToCurrentPlanRoute;
    @NotNull
    Long customerBranchId;

    boolean updated;
}
