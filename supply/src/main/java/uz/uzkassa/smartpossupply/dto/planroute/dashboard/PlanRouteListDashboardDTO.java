package uz.uzkassa.smartpossupply.dto.planroute.dashboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.order.cabinet.CabinetOrderBaseDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteListDashboardDTO {

    Long id;

    LocalDateTime startTime;

    LocalDateTime endTime;

    PlanRouteCustomerBranchStatus status;

    CommonDTO agent;

    CommonDTO planRoute;

    CabinetOrderBaseDTO order;
}
