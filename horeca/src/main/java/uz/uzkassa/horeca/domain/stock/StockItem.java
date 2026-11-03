package uz.uzkassa.horeca.domain.stock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_item")
@Entity
@SQLDelete(sql = "UPDATE stock_item SET deleted='true' WHERE id=?")
public class StockItem extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 34349857L;

    @NotNull
    @Column(name = "stock_id", nullable = false)
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "has_marking", columnDefinition = "boolean default false")
    boolean hasMarking;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;


    @Column(name = "in_kg", scale = 2, precision = 25)
    BigDecimal inKg;

    @Column(name = "total_price", scale = 2, precision = 25)
    BigDecimal totalPrice;

    @Column(name = "vat_rate", scale = 2, precision = 25)
    BigDecimal vatRate;

    @Column(name = "vat_price", scale = 2, precision = 25)
    BigDecimal vatPrice;

    @Column(name = "total_vat_price", scale = 2, precision = 25)
    BigDecimal totalVatPrice;

    @Column(name = "mark_codes")
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> markCodes;

    @Column(name = "aggregation_codes")
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> aggregationCodes;

    @Column(name = "block_codes")
    @JdbcTypeCode(SqlTypes.JSON)
    List<String> blockCodes;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockItem)) {
            return false;
        }
        return id != null && id.equals(((StockItem) o).id);
    }
}
