package uz.uzkassa.smartpos.dto.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class SalesTopBranchDto {
    private Long id;
    private String name;
    private Long count;
    private Long positions;
    private BigDecimal totalCostAmount;
    private BigDecimal totalSum;

    @JsonProperty("totalProfit")
    public BigDecimal totalProfit() {
        return totalSum.subtract(totalCostAmount);
    }

    @JsonProperty("profitPercent")
    public BigDecimal profitPercent() {
        return totalCostAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                totalProfit().multiply(BigDecimal.valueOf(100)).divide(totalCostAmount, 2, RoundingMode.HALF_UP);
    }
}
