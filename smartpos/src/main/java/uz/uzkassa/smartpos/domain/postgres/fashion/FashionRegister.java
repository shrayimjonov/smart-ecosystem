package uz.uzkassa.smartpos.domain.postgres.fashion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class FashionRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long companyId;
    @NotNull
    @Column(name = "branch_id")
    private Long branchId;
    @NotNull
    @Column(name = "city_id")
    private Long cityId;
    @NotNull
    @Column(name = "product_id")
    private Long productId;
    @ManyToOne
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
    private ProductBranch product;
    @NotNull
    @Column(name = "unit_id")
    private Long unitId;
    @ManyToOne
    @JoinColumn(name = "unit_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Unit unit;
    @NotNull
    private BigDecimal qty = BigDecimal.ZERO;
    @NotNull
    private BigDecimal costPrice;
    @NotNull
    private BigDecimal salesPrice;
    @NotNull
    private BigDecimal totalCostPrice;
    @NotNull
    private BigDecimal totalSalesPrice;
    private BigDecimal profit;
    private BigDecimal profitPercentage;
    @Column(name = "product_item_id")
    private Long productItemId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "product_item_id", referencedColumnName = "generic_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductItem productItem;
}
