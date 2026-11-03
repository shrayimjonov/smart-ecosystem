package uz.uzkassa.smartpossupply.dto.agent.performancereport;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AgentPerformanceReportDTO implements Serializable {

    CommonDTO branch;

    CommonDTO agent;

    List<AgentPerformanceReportVisitDTO> visits;

    List<AgentPerformanceReportOrderDTO> orders;

    BigDecimal orderTotalPrice;

    List<AgentPerformanceReportOrderPaymentDTO> payments;

    List<AgentPerformanceReportOrderProductDTO> products;

    List<AgentPerformanceReportOrderCategoryDTO> categories;
}
