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
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.StockItemType;

import java.io.Serial;
import java.math.BigDecimal;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "StockItemLookUpMapper",
                        classes = @ConstructorResult(
                                targetClass = CommonDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class)
                                }
                        )
                )
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "stock_item",
        indexes = {
                @Index(columnList = "stock_id", name = "stock_item_stock_id_idx"),
                @Index(columnList = "unit_id", name = "stock_item_unit_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE stock_item SET deleted='true' WHERE id=?")
public class StockItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 34349857L;

    @NotNull
    @Column(name = "stock_id", nullable = false)
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

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
