package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.BaseEntity;
import uz.uzkassa.cts.domain.stock.StockItem;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variation", indexes = {
        @Index(columnList = "product_id", name = "product_variation_product_id_idx"),
        @Index(columnList = "variation_option_id", name = "product_variation_option_id_idx")
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE product_variation SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 253659242891L;

    @NotNull
    @Column(name = "product_id", nullable = false)
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @NotNull
    @Column(name = "variation_option_id", nullable = false)
    Long variationOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variation_option_id", updatable = false, insertable = false)
    VariationOption variationOption;

}
