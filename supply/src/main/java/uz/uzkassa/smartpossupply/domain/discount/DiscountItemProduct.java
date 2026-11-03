package uz.uzkassa.smartpossupply.domain.discount;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "discount_item_product", indexes = {
        @Index(columnList = "product_id", name = "discount_item_product_product_id_idx"),
        @Index(columnList = "discount_range_id", name = "discount_item_product_discount_range_id_idx"),
})
public class DiscountItemProduct extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 19846684123L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "amount", precision = 25, scale = 2)
    BigDecimal amount;

    @Column(name = "recommended_qty", precision = 25, scale = 2)
    BigDecimal recommendedQty;

    @Column(name = "value_type")
    @Enumerated(value = EnumType.STRING)
    DiscountValueType valueType;

    @Column(name = "discount_limit")
    BigDecimal discountLimit;

    @Column(name = "discount_range_id")
    Long discountRangeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_range_id", updatable = false, insertable = false)
    DiscountRange discountRange;
}
