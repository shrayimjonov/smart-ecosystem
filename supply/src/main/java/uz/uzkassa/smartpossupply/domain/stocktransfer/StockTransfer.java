package uz.uzkassa.smartpossupply.domain.stocktransfer;

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
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.trip.Trip;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.domain.waybill.Waybill;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.StockTransferReason;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.enums.StockTransferType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "StockTransferProductQtyDetailListMapper",
                        classes = @ConstructorResult(
                                targetClass = StockTransferProductQtyDetailListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "stockNumber", type = String.class),
                                        @ColumnResult(name = "stockDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "stockCompletedDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "companyId", type = Long.class),
                                        @ColumnResult(name = "companyName", type = String.class),
                                        @ColumnResult(name = "companyTin", type = String.class),
                                        @ColumnResult(name = "fromWarehouseId", type = Long.class),
                                        @ColumnResult(name = "fromWarehouseName", type = String.class),
                                        @ColumnResult(name = "toWarehouseId", type = Long.class),
                                        @ColumnResult(name = "toWarehouseName", type = String.class)
                                }
                        )
                )
        }
)
@Table(
        name = "stock_transfer",
        indexes = {
                @Index(columnList = "company_id", name = "stock_transfer_company_id_idx"),
                @Index(columnList = "from_branch_id", name = "stock_transfer_from_branch_id_idx"),
                @Index(columnList = "to_branch_id", name = "stock_transfer_to_branch_id_idx"),
                @Index(columnList = "from_warehouse_id", name = "stock_transfer_from_warehouse_id_idx"),
                @Index(columnList = "to_warehouse_id", name = "stock_transfer_to_warehouse_id_idx"),
                @Index(columnList = "transfer_number", name = "stock_transfer_number_idx"),
                @Index(columnList = "deleted", name = "stock_transfer_deleted_idx"),
        }
)
@SQLDelete(sql = "UPDATE stock_transfer SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class StockTransfer extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 465698756L;

    @Column(name = "transfer_number")
    String transferNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "transfer_date")
    LocalDateTime transferDate;

    @Column(name = "completed_date")
    LocalDateTime completedDate;

    @Column(name = "from_branch_id")
    Long fromBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id", updatable = false, insertable = false)
    Branch fromBranch;

    @Column(name = "to_branch_id")
    Long toBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch_id", updatable = false, insertable = false)
    Branch toBranch;

    @Column(name = "from_warehouse_id")
    Long fromWarehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id", updatable = false, insertable = false)
    Warehouse fromWarehouse;

    @Column(name = "to_warehouse_id")
    Long toWarehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id", updatable = false, insertable = false)
    Warehouse toWarehouse;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockTransferStatus status = StockTransferStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    StockTransferReason reason;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "other_reason")
    String otherReason;

    @Column(name = "total_qty", precision = 25, scale = 2)
    BigDecimal totalQty;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "total_brutto", precision = 25, scale = 6)
    BigDecimal totalBrutto;

    @Column(name = "total_netto", precision = 25, scale = 6)
    BigDecimal totalNetto;

    @Column(name = "order_count")
    Integer orderCount;

    @Column(name = "total_block_qty")
    BigDecimal totalBlockQty;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @Column(name = "expeditor_id")
    Long expeditorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expeditor_id", updatable = false, insertable = false)
    User expeditor;

    @OneToMany(mappedBy = "stockTransfer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<StockTransferProduct> products = new ArrayList<>();

    @OneToMany(mappedBy = "stockTransfer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<Waybill> waybills = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_transfer_type")
    StockTransferType stockTransferType = StockTransferType.W2W;

    @Column(name = "trip_id")
    Long tripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", updatable = false, insertable = false)
    Trip trip;

    @Override
    public String getName() {
        return this.getTransferNumber();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.STOCK_TRANSFER;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setTransferNumber(String transferNumber) {
        addLogHistory("transferNumber", this.transferNumber, transferNumber);
        this.transferNumber = transferNumber;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        addLogHistory("transferDate", this.transferDate, transferDate);
        this.transferDate = transferDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        addLogHistory("completedDate", this.completedDate, completedDate);
        this.completedDate = completedDate;
    }

    public void setFromBranch(Branch fromBranch) {
        addLogHistory("fromBranch", this.fromBranch != null ? this.fromBranch.getName() : null, fromBranch != null ? fromBranch.getName() : null);
        this.fromBranch = fromBranch;
    }

    public void setToBranch(Branch toBranch) {
        addLogHistory("toBranch", this.toBranch != null ? this.toBranch.getName() : null, toBranch != null ? toBranch.getName() : null);
        this.toBranch = toBranch;
    }

    public void setFromWarehouse(Warehouse fromWarehouse) {
        addLogHistory("fromWarehouse", this.fromWarehouse != null ? this.fromWarehouse.getName() : null, fromWarehouse != null ? fromWarehouse.getName() : null);
        this.fromWarehouse = fromWarehouse;
    }

    public void setToWarehouse(Warehouse toWarehouse) {
        addLogHistory("toWarehouse", this.toWarehouse != null ? this.toWarehouse.getName() : null, toWarehouse != null ? toWarehouse.getName() : null);
        this.toWarehouse = toWarehouse;
    }

    public void setStatus(StockTransferStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setReason(StockTransferReason reason) {
        addLogHistory("reason", this.reason != null ? this.reason.getName() : null, reason != null ? reason.getName() : null);
        this.reason = reason;
    }

    public void setOtherReason(String otherReason) {
        addLogHistory("otherReason", this.otherReason, otherReason);
        this.otherReason = otherReason;
    }

    public void setTotalQty(BigDecimal totalQty) {
        addLogHistory("totalQty", this.totalQty, totalQty);
        this.totalQty = totalQty;
    }

    public void setDescription(String description) {
        addLogHistory("description", this.description, description);
        this.description = description;
    }
}
