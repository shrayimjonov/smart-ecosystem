package uz.uzkassa.horeca.domain.stock;

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
import uz.uzkassa.horeca.enums.StockStatus;
import uz.uzkassa.horeca.enums.TransferType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock")
@Entity
@SQLDelete(sql = "UPDATE stock SET deleted='true' WHERE id=?")
public class Stock extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 893458349857L;

    @Column(name = "stock_number")
    String stockNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "stock_date")
    LocalDateTime stockDate;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    TransferType transferType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StockStatus status = StockStatus.DRAFT;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "supplier_id")
    Long supplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", updatable = false, insertable = false)
    Company supplier;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "total_price", scale = 2, precision = 25)
    BigDecimal totalPrice;

    @Column(name = "total_qty", scale = 2, precision = 25)
    BigDecimal totalQty;

    @Column(name = "total_vat_price", scale = 2, precision = 25)
    BigDecimal totalVatPrice;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<StockItem> items = new ArrayList<>();

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stock)) {
            return false;
        }
        return id != null && id.equals(((Stock) o).id);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return switch (getTransferType()) {
            case IN -> EntityTypeEnum.STOCK_IN;
            case OUT -> EntityTypeEnum.STOCK_OUT;
            case RETURN -> EntityTypeEnum.STOCK_RETURN;
        };
    }
}
