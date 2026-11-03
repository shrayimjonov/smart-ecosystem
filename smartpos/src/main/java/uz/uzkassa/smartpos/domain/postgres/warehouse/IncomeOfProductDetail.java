package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "income_of_product_detail",
        indexes = {
                @Index(columnList = "income_of_product_id", name = "income_of_product_detail_income_of_product_idx"),
                @Index(columnList = "product_id", name = "income_of_product_detail_product_idx")
        }
)
@Getter
@Setter
public class IncomeOfProductDetail implements ProductQuantityDetail {

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

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "markup")
    private BigDecimal markup;

    @Column(name = "sales_price")
    private BigDecimal salesPrice;

    @Column(name = "qty", scale = 3)
    private BigDecimal qty;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "income_of_product_id")
    private IncomeOfProduct incomeOfProduct;

    @Column(name = "unit_id")
    private Long unitId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Unit unit;

    //    @NotNull
    @Column(name = "product_id"/*, nullable = false*/)
    private Long productId;
    //    @NotNull
    @Column(name = "branch_id"/*, nullable = false*/)
    private Long branchId;
    @Column(name = "city_id")
    private Long cityId;
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
    private ProductBranch product;

    @Getter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "marking_codes", columnDefinition = "jsonb")
    private Set<String> markings;

    public BigDecimal getTotalSum() {
        if (getCostPrice() != null && getQty() != null) {
            return costPrice.multiply(qty);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Integer getMarkingCount() {
        return CollectionUtils.size(markings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomeOfProductDetail that = (IncomeOfProductDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
