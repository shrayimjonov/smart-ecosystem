package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.dto.warehouse.StockAdjustmentItemDTO;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "stock_adjustment_item")
@Getter
@Setter
@DynamicUpdate
public class StockAdjustmentItem implements ProductQuantityDetail {
    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "increment_size",
                            value = "1"
                    )
            }
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequenceGenerator"
    )
    private Long id;

    private BigDecimal qty;

    @NotNull
    @Column(name = "stock_adjustment_id", nullable = false)
    private Long stockAdjustmentId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_id", updatable = false, insertable = false)
    private StockAdjustment stockAdjustment;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @NotNull
    @Column(name = "branch_id")
    private Long branchId;
    @NotNull
    @Column(name = "city_id")
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
            @JoinColumn(name = "city_id", referencedColumnName = "city_id", insertable = false, updatable = false)
    })
    private ProductBranch product;

    @Column(name = "unit_id")
    private Long unitId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Unit unit;

    public StockAdjustmentItemDTO getDTO() {
        StockAdjustmentItemDTO stockAdjustmentItem = new StockAdjustmentItemDTO();
        stockAdjustmentItem.setId(getId());
        stockAdjustmentItem.setQty(getQty());
        if (getProduct() != null) {
            stockAdjustmentItem.setProduct(getProduct().getShortDTO());
        }
        stockAdjustmentItem.setUnitId(getUnitId());
        if (getUnit() != null) {
            stockAdjustmentItem.setUnitName(getUnit().getName());
            stockAdjustmentItem.getProduct().setUnit(getUnit().toDTO());
        }
        if (getStockAdjustment() != null) {
            stockAdjustmentItem.setStockAdjustment(getStockAdjustment().getDTO());
        }
        return stockAdjustmentItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StockAdjustmentItem adjustmentItem = (StockAdjustmentItem) o;
        return id != null && Objects.equals(id, adjustmentItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Integer getMarkingCount() {
        return 0;
    }

    @Override
    public BigDecimal getCostPrice() {
        return null;
    }

    @Override
    public BigDecimal getSalesPrice() {
        return null;
    }
}
