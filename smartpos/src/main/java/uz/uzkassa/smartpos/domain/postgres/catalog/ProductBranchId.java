package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBranchId implements Serializable {
    private Long productId;
    private Long branchId;
    private Long cityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductBranchId that = (ProductBranchId) o;
        return Objects.equals(productId, that.productId) && Objects.equals(branchId, that.branchId) && Objects.equals(cityId, that.cityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, branchId, cityId);
    }
}
