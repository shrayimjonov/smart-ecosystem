package uz.uzkassa.smartpossupply.domain.warehouse;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.dto.product.WarehouseExpeditorMapDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorStatisticsDTO;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.11.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SqlResultSetMappings(
    {
        @SqlResultSetMapping(
            name = "WarehouseExpeditorMapper",
            classes = @ConstructorResult(
                targetClass = WarehouseExpeditorMapDTO.class,
                columns = {
                    @ColumnResult(name = "expeditorId", type = Long.class),
                    @ColumnResult(name = "expeditorFirstName", type = String.class),
                    @ColumnResult(name = "expeditorLastName", type = String.class),
                    @ColumnResult(name = "totalOrderCount", type = Long.class),
                    @ColumnResult(name = "totalCompletedOrderCount", type = Long.class),
                    @ColumnResult(name = "totalQty", type = BigDecimal.class),
                    @ColumnResult(name = "totalSoldQty", type = BigDecimal.class)
                }
            )),
        @SqlResultSetMapping(
            name = "WarehouseExpeditorCarMapper",
            classes = @ConstructorResult(
                targetClass = WarehouseExpeditorMapDTO.class,
                columns = {
                    @ColumnResult(name = "carId", type = Long.class),
                    @ColumnResult(name = "carName", type = String.class),
                    @ColumnResult(name = "carRegNo", type = String.class),
                    @ColumnResult(name = "carModel", type = String.class),
                    @ColumnResult(name = "totalQty", type = BigDecimal.class),
                    @ColumnResult(name = "totalSoldQty", type = BigDecimal.class)
                }
            )),
        @SqlResultSetMapping(
            name = "WarehouseExpeditorStatisticsMapper",
            classes = @ConstructorResult(
                targetClass = WarehouseExpeditorStatisticsDTO.class,
                columns = {
                    @ColumnResult(name = "totalExpeditorCount", type = Long.class),
                    @ColumnResult(name = "totalPlanRouteCount", type = Long.class),
                    @ColumnResult(name = "totalOrderCount", type = Long.class),
                    @ColumnResult(name = "totalCompletedOrderCount", type = Long.class)
                }
            ))

    }
)
@Table(name = "warehouse_expeditor", indexes = {
    @Index(columnList = "warehouse_id", name = "warehouse_expeditor_warehouse_id_idx"),
    @Index(columnList = "car_id", name = "warehouse_expeditor_car_id_idx"),
    @Index(columnList = "expeditor_id", name = "warehouse_expeditor_expeditor_id_idx"),
})
public class WarehouseExpeditor extends SimpleEntity  {

    @Serial
    private static final long serialVersionUID = 984369L;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "car_id")
    Long carId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", updatable = false, insertable = false)
    Warehouse car;

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;

    @Column(name = "expeditor_id")
    Long expeditorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expeditor_id", updatable = false, insertable = false)
    User expeditor;

    @Column(name = "transfer_date")
    LocalDateTime transferDate;
}
