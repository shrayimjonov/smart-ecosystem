package uz.uzkassa.cts.domain.stock;

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
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.inventory.Warehouse;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.StockAdjustmentReason;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_adjustment",
        indexes = {
                @Index(columnList = "adjustment_number", name = "stock_adjustment_number_idx"),
                @Index(columnList = "status", name = "stock_adjustment_status_idx"),
                @Index(columnList = "warehouse_id", name = "stock_adjustment_warehouse_id_idx"),
                @Index(columnList = "branch_id", name = "stock_adjustment_branch_id_idx"),
                @Index(columnList = "company_id", name = "stock_adjustment_company_id_idx"),
        })
@Entity
@SQLDelete(sql = "UPDATE stock_adjustment SET deleted='true' WHERE id=?")
public class StockAdjustment extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 129858385673L;

    @Column(name = "adjustment_number")
    String adjustmentNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "adjustment_date", nullable = false)
    LocalDateTime adjustmentDate;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Column(name = "reason")
    @Enumerated(EnumType.STRING)
    StockAdjustmentReason reason;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    StockStatus status = StockStatus.DRAFT;

    @Column(name = "reject_reason")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String rejectReason;

    @Column(name = "total_qty", scale = 2, precision = 25)
    BigDecimal totalQty;

    @OneToMany(mappedBy = "stockAdjustment", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<StockAdjustmentItem> items = new ArrayList<>();

    public void setAdjustmentNumber(String adjustmentNumber) {
        addChangeLog("adjustmentNumber", this.adjustmentNumber, adjustmentNumber);
        this.adjustmentNumber = adjustmentNumber;
    }

    public void setAdjustmentDate(LocalDateTime adjustmentDate) {
        addChangeLog("transferDate", this.adjustmentDate != null ? this.adjustmentDate.toString() : null, adjustmentDate != null ? adjustmentDate.toString() : null);
        this.adjustmentDate = adjustmentDate;
    }

    public void setDescription(String description) {
        addChangeLog("description", this.description, description);
        this.description = description;
    }

    public void setReason(StockAdjustmentReason reason) {
        addChangeLog("reason", this.reason != null ? this.reason.toString() : null, reason != null ? reason.toString() : null);
        this.reason = reason;
    }

    public void setStatus(StockStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setRejectReason(String rejectReason) {
        addChangeLog("rejectReason", this.rejectReason, rejectReason);
        this.rejectReason = rejectReason;
    }

    public void setTotalQty(BigDecimal totalQty) {
        addChangeLog("totalQty", this.totalQty != null ? this.totalQty.toString() : null, totalQty != null ? totalQty.toString() : null);
        this.totalQty = totalQty;
    }

    public void addItem(StockAdjustmentItem stockAdjustmentItem) {
        stockAdjustmentItem.setStockAdjustment(this);
        stockAdjustmentItem.setStockAdjustmentId(this.id);
        items.add(stockAdjustmentItem);
    }

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT;
    }

    @Override
    public String getName() {
        return getAdjustmentNumber();
    }
}
