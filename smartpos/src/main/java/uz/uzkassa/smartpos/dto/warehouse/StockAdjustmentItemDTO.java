package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class StockAdjustmentItemDTO {
    private Long id;
    private BigDecimal qty;
    private ProductDTO product;
    private Long unitId;
    private String unitName;
    private StockAdjustmentDTO stockAdjustment;
}
