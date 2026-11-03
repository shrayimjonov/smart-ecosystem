package uz.uzkassa.smartpossupply.domain.discount;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;
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
@Table(name = "discount_range", indexes = {
        @Index(columnList = "discount_item_id", name = "discount_range_discount_item_id_idx"),
})
@SQLDelete(sql = "UPDATE discount_range SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class DiscountRange extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 19846684123L;

    @Column(name = "fixed_amount", precision = 25, scale = 2)
    BigDecimal fixedAmount;

    @Column(name = "from_amount", precision = 25, scale = 2)
    BigDecimal fromAmount;

    @Column(name = "to_amount", precision = 25, scale = 2)
    BigDecimal toAmount;

    @Column(name = "discount_item_id", nullable = false)
    Long discountItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_item_id", updatable = false, insertable = false)
    DiscountItem discountItem;

    @OneToMany(mappedBy = "discountRange", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    List<DiscountItemProduct> discountProducts = new ArrayList<>();

    public void addItemProduct(DiscountItemProduct discountItemProduct) {
        discountItemProduct.setDiscountRange(this);
        discountItemProduct.setDiscountRangeId(this.id);
        discountProducts.add(discountItemProduct);
    }
}
