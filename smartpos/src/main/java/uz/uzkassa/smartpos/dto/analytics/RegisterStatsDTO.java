package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class RegisterStatsDTO {
    private Long productCount;
    private BigDecimal totalCostPrice;
    private BigDecimal totalSalesPrice;
}
