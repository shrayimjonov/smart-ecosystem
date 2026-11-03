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
public class KPICalculateDTO {
    Long employeeId;
    String employeeName;
    Long totalCount;
    BigDecimal kpiAmount = BigDecimal.ZERO;

    public KPICalculateDTO(Long employeeId,
                           String employeeName,
                           Long totalCount) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalCount = totalCount;
    }
}
