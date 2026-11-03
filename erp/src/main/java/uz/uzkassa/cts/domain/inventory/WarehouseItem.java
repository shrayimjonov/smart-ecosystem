package uz.uzkassa.cts.domain.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportListDTO;

import java.io.Serial;
import java.math.BigDecimal;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "StockBalanceReportListMapper",
                        classes = @ConstructorResult(
                                targetClass = StockBalanceReportListDTO.class,
                                columns = {
                                        @ColumnResult(name = "itemId", type = Long.class),
                                        @ColumnResult(name = "itemName", type = String.class),
                                        @ColumnResult(name = "warehouseId", type = Long.class),
                                        @ColumnResult(name = "warehouseName", type = String.class),
                                        @ColumnResult(name = "branchId", type = Long.class),
                                        @ColumnResult(name = "branchName", type = String.class),
                                        @ColumnResult(name = "companyId", type = Long.class),
                                        @ColumnResult(name = "companyName", type = String.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class)
                                }
                        )
                )
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "warehouse_item",
        indexes = {
                @Index(columnList = "warehouse_id", name = "warehouse_item_warehouse_id_idx"),
                @Index(columnList = "item_id", name = "warehouse_item_item_id_idx"),
                @Index(columnList = "unit_id", name = "warehouse_item_unit_id_idx")
        }
)
public class WarehouseItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 8966589781123L;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "item_id")
    Long itemId;

    @Column(name = "qty", scale = 2, precision = 25)
    BigDecimal qty = BigDecimal.ZERO;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Reference unit;

    @Column(name = "variation_options", columnDefinition = "bigint[]")
    long[] variationOptions;

    public void addQty(BigDecimal newQty) {
        this.qty = qty.add(newQty);
    }

    public void subtractQty(BigDecimal newQty) {
        this.qty = qty.subtract(newQty);
    }
}
