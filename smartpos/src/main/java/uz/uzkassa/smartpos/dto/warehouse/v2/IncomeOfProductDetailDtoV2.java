package uz.uzkassa.smartpos.dto.warehouse.v2;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.UnitDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProductDetail}
 */
@Getter
@Setter
public class IncomeOfProductDetailDtoV2 implements Serializable {
    private Long id;
    private BigDecimal costPrice;
    private BigDecimal markup;
    private BigDecimal salesPrice;
    private BigDecimal qty;
    private UnitDTO unit;
    private Long productId;
    private String productVatBarcode;
    private String productName;
    private BigDecimal productSalesPrice;
    private BigDecimal productVatRate;
    private boolean productHasMark = false;
    private Set<String> markings;
}