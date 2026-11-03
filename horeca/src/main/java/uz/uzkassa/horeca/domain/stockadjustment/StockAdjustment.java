package uz.uzkassa.horeca.domain.stockadjustment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.StockAdjustmentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "stock_adjustment")
@SQLDelete(sql = "UPDATE stock_adjustment SET deleted = 'true' WHERE id=?")
public class StockAdjustment extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 45654465698756L;

    @NotNull
    @Column(name = "adjustment_number", nullable = false)
    String adjustmentNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @NotNull
    @Column(name = "adjustment_date", nullable = false)
    LocalDateTime adjustmentDate;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockAdjustmentStatus status = StockAdjustmentStatus.DRAFT;

    @Column(name = "total_qty", scale = 2, precision = 25)
    BigDecimal totalQty;

    @Column(name = "total_price", scale = 2, precision = 25)
    BigDecimal totalPrice;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @OneToMany(mappedBy = "stockAdjustment", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<StockAdjustmentItem> items = new ArrayList<>();

    public void addItem(StockAdjustmentItem item) {
        item.setStockAdjustmentId(this.id);
        item.setStockAdjustment(this);
        items.add(item);
    }

    @Override
    public String getName() {
        return this.getAdjustmentNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockAdjustment)) {
            return false;
        }
        return id != null && id.equals(((StockAdjustment) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT;
    }
}
