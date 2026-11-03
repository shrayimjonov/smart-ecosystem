package uz.uzkassa.smartpossupply.domain.productserial;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.01.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_serial_warehouse",
        indexes = {
                @Index(columnList = "warehouse_id", name = "product_serial_warehouse_id_idx"),
                @Index(columnList = "product_serial_id", name = "product_serial_serial_id_idx")
        },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_id", "product_serial_id"})})
public class ProductSerialWarehouse extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 895869L;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @NotNull
    @Column(name = "product_serial_id", nullable = false)
    Long productSerialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @NotNull
    @Column(name = "qty", precision = 25, scale = 2, nullable = false)
    BigDecimal qty = BigDecimal.ZERO;

    public void addQty(BigDecimal newQty) {
        this.qty = qty.add(newQty);
    }

    public void subtractQty(BigDecimal newQty) {
        this.qty = qty.subtract(newQty);
    }
}
