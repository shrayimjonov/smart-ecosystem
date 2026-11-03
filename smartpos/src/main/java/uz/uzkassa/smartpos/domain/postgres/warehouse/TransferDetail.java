package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductItem;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.dto.warehouse.TransferDetailDTO;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;

@Entity
@Table(name = "transfer_detail")
@Getter
@Setter
@DynamicUpdate
public class TransferDetail implements ProductQuantityDetail {
    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            type = SequenceStyleGenerator.class,
            parameters = {
                    @Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @Parameter(
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

    private BigDecimal askedQty;
    private BigDecimal realQty;

    @NotNull
    @Column(name = "transfer_id", nullable = false)
    private Long transferId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", updatable = false, insertable = false)
    private Transfer transfer;

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

    @NotNull
    @Column(name = "unit_id", nullable = false)
    private Long unitId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Unit unit;
    @Column(name = "product_item_id")
    private Long productItemId;
    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "product_item_id", referencedColumnName = "generic_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false),
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductItem productItem;

    private BigDecimal salesPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private HashSet<String> markings;

    public TransferDetailDTO getDTO() {
        TransferDetailDTO dto = new TransferDetailDTO();
        dto.setId(getId());
        dto.setAskedQty(getAskedQty());
        dto.setRealQty(getRealQty());
        dto.setUnitId(getUnitId());
        if (getSalesPrice() != null) {
            dto.setSalesPrice(getSalesPrice());
        }
        if (getUnit() != null) {
            dto.setUnitName(getUnit().getName());
        }
        if (getProduct() != null) {
            dto.setProduct(getProduct().getDTO(false));
        }
        dto.setMarkings(getMarkings());
        return dto;
    }

    @Override
    public BigDecimal getQty() {
        return realQty;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferDetail that = (TransferDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
