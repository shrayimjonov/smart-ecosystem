package uz.uzkassa.smartpossupply.domain.warehouse;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.dto.product.ProductReportListDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockProductStatisticsDTO;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "ProductReportMapper",
                        classes = @ConstructorResult(
                                targetClass = ProductReportListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "barcode", type = String.class),
                                        @ColumnResult(name = "vatBarcode", type = String.class),
                                        @ColumnResult(name = "branchId", type = Long.class),
                                        @ColumnResult(name = "branchName", type = String.class),
                                        @ColumnResult(name = "warehouseId", type = Long.class),
                                        @ColumnResult(name = "warehouseName", type = String.class),
                                        @ColumnResult(name = "basePrice", type = BigDecimal.class),
                                        @ColumnResult(name = "qty", type = BigDecimal.class),
                                        @ColumnResult(name = "vat", type = String.class),
                                        @ColumnResult(name = "entityId", type = Long.class),
                                        @ColumnResult(name = "entityNumber", type = String.class),
                                }
                        )),
                @SqlResultSetMapping(
                        name = "ProductReportStatisticsMapper",
                        classes = @ConstructorResult(
                                targetClass = StockProductStatisticsDTO.class,
                                columns = {@ColumnResult(name = "qty", type = BigDecimal.class), @ColumnResult(name = "dataType", type = String.class)}
                        )
                )

        }
)
@Table(name = "warehouse_product",
        indexes = {
                @Index(columnList = "warehouse_id", name = "warehouse_product_warehouse_id_idx"),
                @Index(columnList = "product_id", name = "warehouse_product_product_id_idx")
        },
        uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_id", "product_id", "unit_id"})})
public class WarehouseProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 895869L;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty = BigDecimal.ZERO;

    @Column(name = "reservation_qty", precision = 25, scale = 2)
    BigDecimal reservationQty = BigDecimal.ZERO;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    public void addQty(BigDecimal newQty) {
        this.qty = qty.add(newQty);
    }

    public void addReservationQty(BigDecimal newQty) {
        this.reservationQty = Optional.ofNullable(reservationQty).orElse(BigDecimal.ZERO).add(newQty);
    }

    public void subtractReservationQty(BigDecimal newQty) {
        BigDecimal result = Optional.ofNullable(reservationQty).orElse(BigDecimal.ZERO)
                .subtract(Optional.ofNullable(newQty).orElse(BigDecimal.ZERO));
        this.reservationQty = result.max(BigDecimal.ZERO);
    }

    public void subtractQty(BigDecimal newQty) {
        this.qty = qty.subtract(newQty);
    }

}
