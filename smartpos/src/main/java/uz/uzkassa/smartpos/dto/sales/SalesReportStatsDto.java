package uz.uzkassa.smartpos.dto.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesReportStatsDto {
    private BigDecimal totalSum;
    private BigDecimal totalCostAmount;
    private BigDecimal avgReceipt;

    @JsonProperty("totalProfit")
    public BigDecimal totalProfit() {
        return totalSum.subtract(totalCostAmount);
    }
}
