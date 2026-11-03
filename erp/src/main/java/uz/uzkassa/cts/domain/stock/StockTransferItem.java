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
@Entity
@Table(name = "stock_transfer_item",
        indexes = {
                @Index(columnList = "stock_transfer_id", name = "stock_transfer_item_stock_transfer_id_idx"),
                @Index(columnList = "from_item_id", name = "stock_transfer_item_from_item_id_idx"),
                @Index(columnList = "to_item_id", name = "stock_transfer_item_to_item_id_idx"),
                @Index(columnList = "unit_id", name = "stock_transfer_item_unit_id_idx"),
                @Index(columnList = "stock_transfer_id", name = "stock_transfer_item_stock_transfer_id_idx")
        })
@SQLDelete(sql = "UPDATE stock_transfer_item SET deleted = 'true' WHERE id=?")
public class StockTransferItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 7666402074L;

    @NotNull
    @Column(name = "from_item_id", nullable = false)
    Long fromItemId;

    @NotNull
    @Column(name = "to_item_id", nullable = false)
    Long toItemId;

    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    StockItemType itemType = StockItemType.PRODUCT;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Reference unit;

    @NotNull
    @Column(name = "qty", scale = 2, precision = 25, nullable = false)
    BigDecimal qty;

    @Column(name = "stock_transfer_id", nullable = false)
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;
}
