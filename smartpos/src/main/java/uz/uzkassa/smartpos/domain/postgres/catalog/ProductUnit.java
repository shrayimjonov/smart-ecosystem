package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.base.audit.AbstractAuditingEntity;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.dto.UnitSelectItem;
import uz.uzkassa.smartpos.dto.catalog.ProductUnitDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(name = "product_unit", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "unit_id", "branch_id"})},
        indexes = {
                @Index(name = "product_unit_product_branch_key", columnList = "product_id, branch_id, city_id"),
                @Index(name = "product_unit_branch_id_key", columnList = "branch_id")
        })
@Getter
@Setter
@DynamicUpdate
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUnit extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Unit unit;
    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;
    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;
    @Column(name = "city_id")
    Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(
                    name = "product_id",
                    referencedColumnName = "product_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(
                    name = "branch_id",
                    referencedColumnName = "branch_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(
                    name = "city_id",
                    referencedColumnName = "city_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    })
    ProductBranch productBranch;

    Integer count = 1;
    Double coefficient = 1.0;
    Integer sorder = 0;
    @Column(columnDefinition = "boolean default false")
    boolean base = false;

    public ProductUnitDTO toDTO() {
        ProductUnitDTO dto = new ProductUnitDTO();
        dto.setId(getId());
        dto.setProductId(getProductId());
        dto.setUnitId(getUnitId());
        if (getUnit() != null) {
            dto.setUnitName(getUnit().getName());
        }
        dto.setCount(getCount());
        dto.setCoefficient(getCoefficient());
        dto.setSorder(getSorder());
        dto.setBase(isBase());
        return dto;
    }

    public UnitSelectItem toSelectItem() {
        UnitSelectItem item = new UnitSelectItem();
        item.setId(getUnitId());
        if (getUnit() != null) {
            item.setName(getUnit().getName());
        }
        item.setBase(isBase());
        if (getProductBranch() != null && getProductBranch().getSalesPrice() != null) {
            item.setPrice(getProductBranch().getSalesPrice()
                    .multiply(BigDecimal.valueOf(Optional.ofNullable(getCoefficient())
                            .orElse(1D))));
            item.setCoefficient(getCoefficient());
        }
        return item;
    }


    public Integer getCount() {
        return count != null ? count : 1;
    }

    public Double getCoefficient() {
        return coefficient != null ? coefficient : 1;
    }

    public Integer getSorder() {
        return sorder != null ? sorder : 0;
    }
}
