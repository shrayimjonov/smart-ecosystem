package uz.uzkassa.smartpossupply.domain.discount;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.enums.DiscountRangeType;
import uz.uzkassa.smartpossupply.enums.DiscountType;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "discount_item", indexes = {
        @Index(columnList = "range_type", name = "discount_item_range_type_idx"),
        @Index(columnList = "discount_type", name = "discount_item_discount_type_idx"),
        @Index(columnList = "product_id", name = "discount_item_product_id_idx"),
        @Index(columnList = "discount_id", name = "discount_item_discount_id_idx"),
})
@SQLDelete(sql = "UPDATE discount_item SET deleted = true WHERE id=?")
public class DiscountItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 19846684123L;

    @Column(name = "discount_type")
    @Enumerated(value = EnumType.STRING)
    DiscountType discountType;

    @Column(name = "range_type")
    @Enumerated(value = EnumType.STRING)
    DiscountRangeType rangeType;

    @Column(name = "apply_all_customers", columnDefinition = "boolean default false")
    boolean applyAllCustomers = false;

    @Column(name = "apply_all_products", columnDefinition = "boolean default false")
    boolean applyAllProducts = false;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Company customer;

    @Column(name = "discount_id", nullable = false)
    Long discountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", updatable = false, insertable = false)
    Discount discount;

    @OneToMany(mappedBy = "discountItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    List<DiscountRange> discountRanges = new ArrayList<>();

    public void addRange(DiscountRange discountRange) {
        discountRange.setDiscountItem(this);
        discountRange.setDiscountItemId(this.id);
        discountRanges.add(discountRange);
    }
}
