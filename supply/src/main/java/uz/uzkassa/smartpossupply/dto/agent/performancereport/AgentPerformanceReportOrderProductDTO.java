package uz.uzkassa.smartpossupply.dto.agent.performancereport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AgentPerformanceReportOrderProductDTO implements Serializable {

    CommonCodeDTO unit;

    Long totalCount;

    @SuppressWarnings("unused")
    public AgentPerformanceReportOrderProductDTO(Long unitId, String unitName, String unitCode, Long totalCount) {
        this.unit = new CommonCodeDTO(unitId, unitName, unitCode);
        this.totalCount = totalCount;
    }
}
