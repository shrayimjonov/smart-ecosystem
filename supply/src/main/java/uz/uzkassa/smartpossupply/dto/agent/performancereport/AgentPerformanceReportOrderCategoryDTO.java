package uz.uzkassa.smartpossupply.dto.agent.performancereport;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AgentPerformanceReportOrderCategoryDTO implements Serializable {

    CommonDTO category;

    List<AgentPerformanceReportOrderCategoryUnitDTO> units;
}
