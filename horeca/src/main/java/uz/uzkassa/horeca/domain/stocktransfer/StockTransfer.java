package uz.uzkassa.horeca.domain.stocktransfer;

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
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.StockTransferStatus;
import uz.uzkassa.horeca.enums.StockTransferType;

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
@Table(
        name = "stock_transfer",
        indexes = {
                @Index(columnList = "from_branch_id", name = "stock_transfer_from_branch_id_idx"),
                @Index(columnList = "to_branch_id", name = "stock_transfer_to_branch_id_idx"),
                @Index(columnList = "from_warehouse_id", name = "stock_transfer_from_warehouse_id_idx"),
                @Index(columnList = "to_warehouse_id", name = "stock_transfer_to_warehouse_id_idx"),
                @Index(columnList = "transfer_number", name = "stock_transfer_number_idx"),
        }
)
@SQLDelete(sql = "UPDATE stock_transfer SET deleted = 'true' WHERE id=?")
public class StockTransfer extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5473433L;

    @Column(name = "transfer_number")
    String transferNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "transfer_date")
    LocalDateTime transferDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    StockTransferType transferType;

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

    @Column(name = "total_price", scale = 2, precision = 25)
    BigDecimal totalPrice;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @OneToMany(mappedBy = "stockTransfer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<StockTransferItem> items = new ArrayList<>();

    @Column(name = "reject_reason")
    String rejectReason;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockTransfer)) {
            return false;
        }
        return id != null && id.equals(((StockTransfer) o).id);
    }
}
