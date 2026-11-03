package uz.uzkassa.smartpos.dto.warehouse.v2;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProductDetail}
 */
@Getter
@Setter
public class IncomeOfProductDetailCreateDto implements Serializable {
    private Long id;
    private BigDecimal costPrice;
    private BigDecimal markup;
    private BigDecimal salesPrice;
    @NotNull
    private BigDecimal qty;
    private Long unitId;
    private Long productId;
    private Set<String> markings;

    public Long getUnitId() {
        return unitId != null ? unitId : 2L;
    }
}