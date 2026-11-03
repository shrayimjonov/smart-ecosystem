package uz.uzkassa.cts.domain.stock;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.base.BaseEntity;
import uz.uzkassa.cts.enums.StockItemType;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_adjustment_item",
        indexes = {
                @Index(columnList = "stock_adjustment_id", name = "stock_adjustment_item_stock_adjustment_id_idx"),
                @Index(columnList = "item_id", name = "stock_adjustment_item_item_id_idx"),
                @Index(columnList = "unit_id", name = "stock_adjustment_item_unit_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE stock_adjustment_item SET deleted='true' WHERE id=?")
public class StockAdjustmentItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8950437394L;

    @NotNull
    @Column(name = "stock_adjustment_id", nullable = false)
    Long stockAdjustmentId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_adjustment_id", updatable = false, insertable = false)
    StockAdjustment stockAdjustment;

    @NotNull
    @Column(name = "item_id", nullable = false)
    Long itemId;

    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    StockItemType itemType = StockItemType.PRODUCT;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Reference unit;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty;
}
