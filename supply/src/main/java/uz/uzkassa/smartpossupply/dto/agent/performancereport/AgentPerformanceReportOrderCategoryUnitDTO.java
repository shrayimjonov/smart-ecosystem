package uz.uzkassa.smartpossupply.dto.agent.performancereport;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class AgentPerformanceReportOrderCategoryUnitDTO implements Serializable {

    CommonDTO unit;

    BigDecimal totalCount;
}
