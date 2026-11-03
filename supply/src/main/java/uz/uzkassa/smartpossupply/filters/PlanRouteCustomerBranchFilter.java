package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlanRouteCustomerBranchFilter extends SimpleFilter {
    Long agentId;
    Long planRouteId;
    String status;
    Long customerBranchId;
    LocalDate visitPlanDate;
}
