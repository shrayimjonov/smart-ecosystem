package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductUnit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.catalog.ProductUnit}
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductUnitDTO implements Serializable {
    private Long id;
    private Long productId;
    @NotNull
    private Long unitId;
    private String unitName;
    @NotNull
    private Integer count;
    private Integer sorder;
    private Double coefficient;
    private boolean base;
    private BigDecimal price;

    public ProductUnitDTO(Long productId, Long unitId) {
        this.productId = productId;
        this.unitId = unitId;
    }

    public ProductUnitDTO(ProductUnit productUnit, BigDecimal basePrice) {
        this.id = productUnit.getId();
        this.productId = productUnit.getProductId();
        this.unitId = productUnit.getUnitId();
        if (productUnit.getUnit() != null) {
            this.unitName = productUnit.getUnit().getName();
        }
        this.count = productUnit.getCount();
        this.sorder = productUnit.getSorder();
        this.coefficient = productUnit.getCoefficient();
        this.base = productUnit.isBase();
        if (basePrice != null && coefficient != null) {
            this.price = basePrice.multiply(new BigDecimal(coefficient));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductUnitDTO that = (ProductUnitDTO) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(unitId, that.unitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, unitId);
    }
}
