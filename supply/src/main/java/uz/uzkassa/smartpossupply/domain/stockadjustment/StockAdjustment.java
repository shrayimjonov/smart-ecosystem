package uz.uzkassa.smartpossupply.domain.stockadjustment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "stock_adjustment",
        indexes = {
                @Index(columnList = "company_id", name = "stock_adjustment_company_id_idx"),
                @Index(columnList = "branch_id", name = "stock_adjustment_branch_id_idx"),
                @Index(columnList = "warehouse_id", name = "stock_adjustment_warehouse_id_idx"),
                @Index(columnList = "adjustment_number", name = "stock_adjustment_number_idx"),
                @Index(columnList = "deleted", name = "stock_adjustment_deleted_idx"),
        }
)
@SQLDelete(sql = "UPDATE stock_adjustment SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class StockAdjustment extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 45654465698756L;

    @Column(name = "adjustment_number")
    String adjustmentNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "adjustment_date")
    LocalDateTime adjustmentDate;

    @Column(name = "completed_date")
    LocalDateTime completedDate;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockAdjustmentStatus status = StockAdjustmentStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason")
    AdjustmentRejectReason rejectReason;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "reject_other_reason")
    String rejectOtherReason;

    @Column(name = "total_qty", precision = 25, scale = 2)
    BigDecimal totalQty;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "total_foreign_price", precision = 25, scale = 2)
    BigDecimal totalForeignPrice;

    @Column(name = "exchange_rate", precision = 25, scale = 2)
    BigDecimal exchangeRate;

    @Column(name = "currency_type")
    @Enumerated(EnumType.STRING)
    CurrencyType currencyType;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "adjustment_reason", columnDefinition = "varchar(255) default 'OTHER'")
    @Enumerated(EnumType.STRING)
    AdjustmentReason adjustmentReason = AdjustmentReason.OTHER;

    @Column(name = "document_number")
    String documentNumber;

    @Column(name = "document_date")
    LocalDate documentDate;

    @Column(name = "document_type", columnDefinition = "varchar(255) default 'OTHER'")
    @Enumerated(EnumType.STRING)
    DocumentType documentType = DocumentType.OTHER;

    @OneToMany(mappedBy = "stockAdjustment", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<StockAdjustmentProduct> products = new ArrayList<>();

    @Override
    public String getName() {
        return this.getAdjustmentNumber();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_ADJUSTMENT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setAdjustmentNumber(String adjustmentNumber) {
        addLogHistory("adjustmentNumber", this.adjustmentNumber, adjustmentNumber);
        this.adjustmentNumber = adjustmentNumber;
    }

    public void setAdjustmentDate(LocalDateTime adjustmentDate) {
        addLogHistory("adjustmentDate", this.adjustmentDate, adjustmentDate);
        this.adjustmentDate = adjustmentDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        addLogHistory("completedDate", this.completedDate, completedDate);
        this.completedDate = completedDate;
    }

    public void setBranch(Branch branch) {
        addLogHistory("branch", this.branch != null ? branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setWarehouse(Warehouse warehouse) {
        addLogHistory("warehouse", this.warehouse != null ? warehouse.getName() : null, warehouse != null ? warehouse.getName() : null);
        this.warehouse = warehouse;
    }

    public void setStatus(StockAdjustmentStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setRejectReason(AdjustmentRejectReason rejectReason) {
        addLogHistory("rejectReason", this.rejectReason != null ? this.rejectReason.getName() : null, rejectReason != null ? rejectReason.getName() : null);
        this.rejectReason = rejectReason;
    }

    public void setRejectOtherReason(String rejectOtherReason) {
        addLogHistory("rejectOtherReason", this.rejectOtherReason, rejectOtherReason);
        this.rejectOtherReason = rejectOtherReason;
    }

    public void setTotalQty(BigDecimal totalQty) {
        addLogHistory("totalQty", this.totalQty, totalQty);
        this.totalQty = totalQty;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        addLogHistory("totalPrice", this.totalPrice, totalPrice);
        this.totalPrice = totalPrice;
    }

    public void setDescription(String description) {
        addLogHistory("description", this.description, description);
        this.description = description;
    }
}
