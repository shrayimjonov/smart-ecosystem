package uz.uzkassa.smartpos.dto.warehouse.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransferDetailFashionDto {
    private Long id;
    private Long productId;
    private String productName;
    private String colorCode;
    private String colorName;
    private String printName;
    private String sizeCode;
    private String sizeValue;
    private BigDecimal askedQty;
    private BigDecimal realQty;
    private CommonDTO unit;
}
