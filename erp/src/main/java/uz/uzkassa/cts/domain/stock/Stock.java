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
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.domain.inventory.Warehouse;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportStatisticItemDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "StockBalanceReportStatisticItemMapper",
                        classes = @ConstructorResult(
                                targetClass = StockBalanceReportStatisticItemDTO.class,
                                columns = {
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "dataType", type = String.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "StockBalanceReportDetailListMapper",
                        classes = @ConstructorResult(
                                targetClass = StockBalanceReportDetailListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "stockNumber", type = String.class),
                                        @ColumnResult(name = "stockDate", type = LocalDateTime.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class)
                                }
                        )
                )
        }
)
@Table(name = "stock",
        indexes = {
                @Index(columnList = "stock_number", name = "stock_number_idx"),
                @Index(columnList = "stock_date", name = "stock_date_idx"),
                @Index(columnList = "status", name = "stock_status_idx"),
                @Index(columnList = "transfer_type", name = "stock_transfer_type_idx"),
                @Index(columnList = "warehouse_id", name = "stock_warehouse_id_idx"),
                @Index(columnList = "branch_id", name = "stock_branch_id_idx"),
                @Index(columnList = "company_id", name = "stock_company_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE stock SET deleted='true' WHERE id=?")
public class Stock extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 893458349857L;

    @Column(name = "stock_number")
    String stockNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "stock_date")
    LocalDateTime stockDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    StockTransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockStatus status = StockStatus.DRAFT;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;

    @Column(name = "department_id")
    Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    Department department;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<StockItem> items = new ArrayList<>();

    public void setStockNumber(String stockNumber) {
        addChangeLog("stockNumber", this.stockNumber, stockNumber);
        this.stockNumber = stockNumber;
    }

    public void setStockDate(LocalDateTime stockDate) {
        addChangeLog("stockDate", this.stockDate != null ? this.stockDate.toString() : null, stockDate != null ? stockDate.toString() : null);
        this.stockDate = stockDate;
    }

    public void setTransferType(StockTransferType transferType) {
        addChangeLog("transferType", this.transferType != null ? this.transferType.toString() : null, transferType != null ? transferType.toString() : null);
        this.transferType = transferType;
    }

    public void setStatus(StockStatus status) {
        addChangeLog("status", this.status != null ? status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setDescription(String description) {
        addChangeLog("description", this.description, description);
        this.description = description;
    }

    public void addItem(StockItem stockItem) {
        stockItem.setStock(this);
        stockItem.setStockId(this.id);
        items.add(stockItem);
    }

    @Override
    public String getName() {
        return this.stockNumber;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return switch (getTransferType()) {
            case IN -> EntityTypeEnum.STOCK_IN;
            case OUT -> EntityTypeEnum.STOCK_OUT;
            case RETURN -> EntityTypeEnum.STOCK_RETURN;
            case ADJUSTMENT -> EntityTypeEnum.STOCK_ADJUSTMENT;
            case TRANSFER_FROM, TRANSFER_TO -> EntityTypeEnum.STOCK_TRANSFER;
        };
    }

    @Override
    public String getEntityName() {
        return this.getName();
    }
}
