package uz.uzkassa.smartpos.domain.postgres.receipt;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExtraDetail {
    private String packageCode;
    private String packageName;
    private Long productItemId;
    private String colorName;
    private String sizeValue;
    private String printName;
    private BigDecimal price;
    private BigDecimal advancePrice;
    private BigDecimal advancePercent;
}
