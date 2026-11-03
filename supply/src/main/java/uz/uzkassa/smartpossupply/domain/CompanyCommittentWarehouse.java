package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company_committent_warehouse",
        indexes = {
                @Index(columnList = "warehouse_id", name = "company_committent_warehouse_id_idx"),
                @Index(columnList = "company_committent_id", name = "company_committent_warehouse_committent_id_id_idx"),
                @Index(columnList = "product_id", name = "company_committent_warehouse_product_id_idx")
        },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_id", "company_committent_id", "product_id"})}
)
public class CompanyCommittentWarehouse extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 895869L;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @NotNull
    @Column(name = "company_committent_id", nullable = false)
    Long companyCommittentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_committent_id", updatable = false, insertable = false)
    CompanyCommittent companyCommittent;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

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
