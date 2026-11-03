package uz.uzkassa.smartpossupply.dto.planroute.dashboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteDashboardPlanRouteDetailDTO implements Serializable {

    Long id;

    CommonDTO customerBranch;

    LocalDateTime startDate;

    LocalDateTime endDate;

    PlanRouteCustomerBranchStatus status;

    CommonDTO agent;
}
