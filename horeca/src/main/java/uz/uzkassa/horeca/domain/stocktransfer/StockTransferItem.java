package uz.uzkassa.horeca.domain.stocktransfer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "stock_transfer_item",
        indexes = {
                @Index(columnList = "stock_transfer_id", name = "stock_transfer_item_stock_transfer_id_idx"),
                @Index(columnList = "from_product_id", name = "stock_transfer_item_from_product_id_idx"),
                @Index(columnList = "to_product_id", name = "stock_transfer_item_to_product_id_idx"),
                @Index(columnList = "unit_id", name = "stock_transfer_item_unit_id_idx"),
                @Index(columnList = "stock_transfer_id", name = "stock_transfer_item_stock_transfer_id_idx")
        })
@SQLDelete(sql = "UPDATE stock_transfer_item SET deleted = 'true' WHERE id=?")
public class StockTransferItem extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7773478323L;

    @NotNull
    @Column(name = "from_product_id", nullable = false)
    Long fromProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_product_id", updatable = false, insertable = false)
    Product fromProduct;

    @NotNull
    @Column(name = "to_product_id", nullable = false)
    Long toProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_product_id", updatable = false, insertable = false)
    Product toProduct;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @NotNull
    @Column(name = "qty", scale = 2, precision = 25, nullable = false)
    BigDecimal qty;

    @NotNull
    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "in_kg", scale = 2, precision = 25)
    BigDecimal inKg;

    @Column(name = "created_date")
    LocalDate createdDate;

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

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockTransferItem)) {
            return false;
        }
        return id != null && id.equals(((StockTransferItem) o).id);
    }
}
