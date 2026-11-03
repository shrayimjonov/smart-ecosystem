package uz.uzkassa.smartpossupply.dto.agent.performancereport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AgentPerformanceReportOrderDTO implements Serializable {

    OrderStatus status;

    Long totalCount;

    @SuppressWarnings("unused")
    public AgentPerformanceReportOrderDTO(String status, Long totalCount) {
        this.status = OrderStatus.valueOf(status);
        this.totalCount = totalCount;
    }
}
