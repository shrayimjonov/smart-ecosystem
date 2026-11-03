package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeSalesStatDTO {
    private LocalDateTime firstDayAtWork = LocalDateTime.now();

    private BigDecimal totalSales = BigDecimal.ZERO;

    private BigDecimal totalRevenue = BigDecimal.ZERO;

    private BigDecimal averageCheckAmount = BigDecimal.ZERO;
}

