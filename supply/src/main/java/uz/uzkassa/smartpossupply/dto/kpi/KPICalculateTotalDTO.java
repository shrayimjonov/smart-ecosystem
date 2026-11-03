package uz.uzkassa.smartpossupply.dto.kpi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPICalculateTotalDTO {
    Long countEmployees;
    Long totalCount;
    Long visitedCount;
    BigDecimal totalKpiAmount = BigDecimal.ZERO;

    public KPICalculateTotalDTO(Long countEmployees, Long visitedCount, Long totalCount) {
        this.countEmployees = countEmployees;
        this.visitedCount = visitedCount;
        this.totalCount = totalCount;
    }

    public KPICalculateTotalDTO(Long countEmployees, Long totalCount) {
        this.countEmployees = countEmployees;
        this.totalCount = totalCount;
    }
}
