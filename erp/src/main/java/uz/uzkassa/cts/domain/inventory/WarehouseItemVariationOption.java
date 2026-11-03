package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.stock.StockItemVariationOption;
import uz.uzkassa.cts.enums.StockTransferType;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "warehouse_item_variation_option")
public class WarehouseItemVariationOption extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5588956281936L;

    @Column(name = "warehouse_item_id", nullable = false)
    Long warehouseItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_item_id", updatable = false, insertable = false)
    WarehouseItem warehouseItem;

    @Column(name = "stock_item_variation_option_id", nullable = false)
    Long stockItemVariationOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_item_variation_option_id", updatable = false, insertable = false)
    StockItemVariationOption stockItemVariationOption;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type")
    StockTransferType transferType;
}
