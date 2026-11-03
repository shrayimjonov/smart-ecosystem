package uz.uzkassa.smartpos.dto.integration.onec;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OneCIncomeDetailDto {
    private OneCIncomeProductDto product;
    private Long unitId;
    private String unitName;
    private BigDecimal qty;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
}
