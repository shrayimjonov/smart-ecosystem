package uz.uzkassa.smartpos.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class RegisterDto {
    private Long productId;
    private String productName;
    private String mxik;
    private Long branchId;
    private String branchName;
    private Long unitId;
    private String unitName;
    private BigDecimal qty;
    private Integer markingCount;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private BigDecimal totalCostPrice;
    private BigDecimal totalSalesPrice;
    private boolean hasMark;

    @JsonProperty("profit")
    public BigDecimal getProfit() {
        return totalSalesPrice.subtract(totalCostPrice);
    }

    @JsonProperty("margin")
    public BigDecimal getMargin() {
        return BigDecimal.ZERO.compareTo(totalCostPrice) < 0 ? getProfit().divide(totalCostPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
    }
}
