package uz.uzkassa.smartpos.dto.sales;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesDetailDynamicsDto {
    private BigDecimal averageDiscountPercent;
    private BigDecimal averageQty;
    private Long count;
    private Long returnedCount;
}
