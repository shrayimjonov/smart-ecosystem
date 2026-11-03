package uz.uzkassa.smartpossupply.dto.kpi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPIPlanRouteCalculateDTO extends KPICalculateDTO {
    Long planRouteId;
    String planRouteName;
    Long visitedCount;
    BigDecimal kpiPercentage;


    public KPIPlanRouteCalculateDTO(Long planRouteId,
                                    String planRouteName,
                                    Long employeeId,
                                    String employeeName,
                                    Long visitedCount,
                                    Long totalCount) {
        super(employeeId, employeeName, totalCount);
        this.planRouteId = planRouteId;
        this.planRouteName = planRouteName;
        this.visitedCount = visitedCount;
        this.kpiPercentage = BigDecimal.valueOf((double) visitedCount / totalCount * 100.0);
    }
}
