package uz.uzkassa.horeca.domain.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.product.Product;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "warehouse_item",
    indexes = {
        @Index(columnList = "warehouse_id", name = "warehouse_item_warehouse_id_idx"),
        @Index(columnList = "product_id", name = "warehouse_item_product_id_idx"),
        @Index(columnList = "unit_id", name = "warehouse_item_unit_id_idx"),
    },
    uniqueConstraints = { @UniqueConstraint(columnNames = { "warehouse_id", "product_id", "unit_id", "created_date", "price" }) }
)
public class WarehouseItem extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3432895869L;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty = BigDecimal.ZERO;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Column(name = "price", scale = 2, precision = 25)
    BigDecimal price;

    @Column(name = "in_kg", scale = 2, precision = 25)
    BigDecimal inKg;

    @Column(name = "created_date")
    LocalDate createdDate;

    public void addQty(BigDecimal newQty) {
        this.qty = qty.add(newQty);
    }

    public void addInKg(BigDecimal newInKg) {
        this.inKg = inKg.add(newInKg);
    }

    public void subtractQty(BigDecimal newQty) {
        this.qty = qty.subtract(newQty);
    }

    public void subtractInKg(BigDecimal newInKg) {
        this.inKg = inKg.subtract(newInKg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WarehouseItem)) {
            return false;
        }
        return id != null && id.equals(((WarehouseItem) o).id);
    }
}
