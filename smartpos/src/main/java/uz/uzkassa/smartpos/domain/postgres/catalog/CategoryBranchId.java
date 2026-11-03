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
public class CategoryBranchId implements Serializable {
    private Long categoryId;
    private Long branchId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CategoryBranchId that = (CategoryBranchId) o;
        return Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(branchId, that.branchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, branchId);
    }
}
