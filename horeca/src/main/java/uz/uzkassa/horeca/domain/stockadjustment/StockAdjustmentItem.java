package uz.uzkassa.horeca.domain.stockadjustment;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "stock_adjustment_item")
@SQLDelete(sql = "UPDATE stock_adjustment_item SET deleted = 'true' WHERE id=?")
public class StockAdjustmentItem extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 976541329094756L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "qty", scale = 2, precision = 25, nullable = false)
    BigDecimal qty;

    @Column(name = "price", scale = 2, precision = 25, nullable = false)
    BigDecimal price;

    @Column(name = "mark_codes")
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> markCodes;

    @Column(name = "aggregation_codes")
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> aggregationCodes;

    @Column(name = "block_codes")
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> blockCodes;

    @NotNull
    @Column(name = "stock_adjustment_id", nullable = false)
    Long stockAdjustmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_id", updatable = false, insertable = false)
    StockAdjustment stockAdjustment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockAdjustmentItem)) {
            return false;
        }
        return id != null && id.equals(((StockAdjustmentItem) o).id);
    }


}
