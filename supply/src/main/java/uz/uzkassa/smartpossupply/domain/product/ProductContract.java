package uz.uzkassa.smartpossupply.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_contract", indexes = {
        @Index(columnList = "name", name = "product_contract_name_idx"),
        @Index(columnList = "product_id", name = "product_contract_product_id_idx"),
        @Index(columnList = "branch_id", name = "product_contract_branch_id_idx"),
}, uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "base_price", "profit_rate, product_id"})})
@SQLDelete(sql = "UPDATE product_contract SET deleted = true WHERE id=?")
public class ProductContract extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1684995613L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @NotNull
    @Column(name = "base_price", precision = 25, scale = 2, nullable = false)
    BigDecimal basePrice;

    @NotNull
    @Column(name = "profit_rate", precision = 25, scale = 2, nullable = false)
    BigDecimal profitRate;

    @NotNull
    @Column(name = "price", precision = 25, scale = 2, nullable = false)
    BigDecimal price;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;
}
