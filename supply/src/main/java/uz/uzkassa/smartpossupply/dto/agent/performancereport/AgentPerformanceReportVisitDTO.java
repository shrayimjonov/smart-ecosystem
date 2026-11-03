package uz.uzkassa.smartpossupply.dto.agent.performancereport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AgentPerformanceReportVisitDTO implements Serializable {

    PlanRouteCustomerBranchStatus status;

    Long totalCount;

    @SuppressWarnings("unused")
    public AgentPerformanceReportVisitDTO(String status, Long totalCount) {
        this.status = PlanRouteCustomerBranchStatus.valueOf(status);
        this.totalCount = totalCount;
    }
}
