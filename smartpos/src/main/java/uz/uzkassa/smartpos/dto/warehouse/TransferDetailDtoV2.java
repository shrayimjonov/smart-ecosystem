package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransferDetailDtoV2 {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal askedQty;
    private BigDecimal realQty;
    private CommonDTO unit;
}
