package uz.uzkassa.smartpos.dto.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail;
import uz.uzkassa.smartpos.dto.UnitDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail}
 */
@Getter
@Setter
@NoArgsConstructor
public class FashionIncomeDetailDto extends FashionIncomeBaseDto implements Serializable {
    private Long id;
    private Long productId;
    private String productName;
    private String productBarcode;
    private UnitDTO unit;
    private BigDecimal qty;
    private BigDecimal costPrice;
    private BigDecimal salesPrice;
    private Set<FashionIncomeColorGroupDto> colorGroups = new HashSet<>();

    public FashionIncomeDetailDto(FashionIncomeOfProductDetail detail) {
        super(detail);
        this.id = detail.getId();
        this.productId = detail.getProduct().getProductId();
        this.productName = detail.getProduct().getName();
        this.productBarcode = detail.getProduct().getBarcode();
        this.unit = detail.getUnit().toDTO();
        this.qty = detail.getQty();
        this.costPrice = detail.getCostPrice();
        this.salesPrice = detail.getSalesPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FashionIncomeDetailDto that = (FashionIncomeDetailDto) o;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }
}