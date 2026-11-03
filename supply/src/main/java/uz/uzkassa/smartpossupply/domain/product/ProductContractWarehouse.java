package uz.uzkassa.smartpossupply.domain.product;

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

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_contract_warehouse",
        indexes = {
                @Index(columnList = "warehouse_id", name = "product_contract_warehouse_id_idx"),
                @Index(columnList = "product_contract_id", name = "product_contract_contract_id_idx")
        },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_id", "product_contract_id"})})
public class ProductContractWarehouse extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 895869L;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @NotNull
    @Column(name = "product_contract_id", nullable = false)
    Long productContractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_contract_id", updatable = false, insertable = false)
    ProductContract productContract;

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
