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
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "StockTransferItemQtyDetailListMapper",
                        classes = @ConstructorResult(
                                targetClass = StockBalanceReportDetailListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "stockNumber", type = String.class),
                                        @ColumnResult(name = "stockDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "fromWarehouseId", type = Long.class),
                                        @ColumnResult(name = "fromWarehouseName", type = String.class),
                                        @ColumnResult(name = "toWarehouseId", type = Long.class),
                                        @ColumnResult(name = "toWarehouseName", type = String.class)
                                }
                        )
                )
        }
)

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "stock_transfer",
        indexes = {
                @Index(columnList = "from_branch_id", name = "stock_transfer_from_branch_id_idx"),
                @Index(columnList = "to_branch_id", name = "stock_transfer_to_branch_id_idx"),
                @Index(columnList = "from_warehouse_id", name = "stock_transfer_from_warehouse_id_idx"),
                @Index(columnList = "to_warehouse_id", name = "stock_transfer_to_warehouse_id_idx"),
                @Index(columnList = "transfer_number", name = "stock_transfer_number_idx"),
                @Index(columnList = "company_id", name = "stock_transfer_company_id_idx")
        }
)
@SQLDelete(sql = "UPDATE stock_transfer SET deleted = 'true' WHERE id=?")
public class StockTransfer extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 5473433L;

    @Column(name = "transfer_number")
    String transferNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "transfer_date")
    LocalDateTime transferDate;

    @NotNull
    @Column(name = "from_branch_id", nullable = false)
    Long fromBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id", updatable = false, insertable = false)
    Branch fromBranch;

    @NotNull
    @Column(name = "to_branch_id", nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockTransferStatus status = StockTransferStatus.DRAFT;

    @Column(name = "total_qty", scale = 2, precision = 25)
    BigDecimal totalQty;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "stockTransfer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<StockTransferItem> items = new ArrayList<>();

    public void setTransferNumber(String transferNumber) {
        addChangeLog("transferNumber", this.transferNumber, transferNumber);
        this.transferNumber = transferNumber;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        addChangeLog("transferDate", this.transferDate != null ? this.transferDate.toString() : null, transferDate != null ? transferDate.toString() : null);
        this.transferDate = transferDate;
    }

    public void addItem(StockTransferItem stockTransferItem) {
        stockTransferItem.setStockTransferId(this.id);
        stockTransferItem.setStockTransfer(this);
        items.add(stockTransferItem);
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
    public String getName() {
        return getTransferNumber();
    }
}
