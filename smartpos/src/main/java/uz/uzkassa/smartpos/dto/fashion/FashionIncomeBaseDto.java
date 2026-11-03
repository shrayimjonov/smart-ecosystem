package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
public class FashionIncomeBaseDto {
    private BigDecimal totalCostPrice;
    private BigDecimal totalSalesPrice;

    public FashionIncomeBaseDto(FashionIncomeOfProductDetail detail) {
        this.totalCostPrice = detail.getTotalCostPrice();
        this.totalSalesPrice = detail.getTotalSalesPrice();
    }

    public BigDecimal getProfitPercentage() {
        return totalSalesPrice.subtract(totalCostPrice).multiply(new BigDecimal(100)).divide(totalCostPrice, 4, RoundingMode.HALF_UP);
    }
}
