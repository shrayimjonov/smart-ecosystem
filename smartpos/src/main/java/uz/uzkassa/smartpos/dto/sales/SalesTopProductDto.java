package uz.uzkassa.smartpos.dto.sales;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesTopProductDto {
    private Long id;
    private String name;
    private BigDecimal totalAmount;
    private BigDecimal totalCostAmount;
    private BigDecimal totalQty;
}
