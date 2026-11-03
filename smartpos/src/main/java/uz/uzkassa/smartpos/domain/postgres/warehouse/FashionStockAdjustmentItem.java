package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fashion_stock_adjustment_item")
@Getter
@Setter
@DynamicUpdate
public class FashionStockAdjustmentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    @Column(name = "created_by_id")
    private Long createdById;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private User createdBy;
    @NotNull
    private Long companyId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;
    @Column(name = "city_id")
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
            @JoinColumn(name = "city_id", referencedColumnName = "city_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductBranch product;
    @Column(name = "product_item_id")
    private Long productItemId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "product_item_id", referencedColumnName = "generic_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductItem productItem;
    @NotNull
    @Column(name = "unit_id")
    private Long unitId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    private Unit unit;
    private BigDecimal qty;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StockAdjustmentReason reason;
}
