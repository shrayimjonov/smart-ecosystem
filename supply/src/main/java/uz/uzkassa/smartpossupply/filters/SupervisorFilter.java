package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SupervisorFilter extends SimpleFilter {
    Long supervisorId;
    Long planRouteId;
    Long agentId;
    Long customerBranchId;
    String role;
    Long categoryId;
    Long expeditorId;
    LocalDate visitPlanDate;
}
