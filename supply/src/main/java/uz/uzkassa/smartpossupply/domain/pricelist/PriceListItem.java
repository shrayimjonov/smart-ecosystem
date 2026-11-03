package uz.uzkassa.smartpossupply.domain.pricelist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.product.Product;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 13:29
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "price_list_item", indexes = {
        @Index(columnList = "product_id", name = "price_list_item_product_id_idx"),
        @Index(columnList = "price_list_id", name = "price_list_price_list_id_idx")
})
@Entity
@SQLDelete(sql = "UPDATE price_list_item SET deleted = true WHERE id=?")
public class PriceListItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 755783L;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    Product product;

    @Column(name = "price_list_id")
    Long priceListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", insertable = false, updatable = false)
    PriceList priceList;
}