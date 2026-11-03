package uz.uzkassa.cts.dto.stock.balancereport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockBalanceReportDetailListDTO implements Serializable {

    Long id;

    String stockNumber;

    LocalDateTime stockDate;

    BigDecimal qty;

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    @SuppressWarnings("unused")
    public StockBalanceReportDetailListDTO(Long id, String stockNumber, LocalDateTime stockDate, BigDecimal qty) {
        this.id = id;
        this.stockNumber = stockNumber;
        this.stockDate = stockDate;
        this.qty = qty;
    }

    @SuppressWarnings("unused")
    public StockBalanceReportDetailListDTO(Long id, String stockNumber, LocalDateTime stockDate, BigDecimal qty, Long fromWarehouseId, String fromWarehouseName, Long toWarehouseId, String toWarehouseName) {
        this.id = id;
        this.stockNumber = stockNumber;
        this.stockDate = stockDate;
        this.qty = qty;
        this.fromWarehouse = new CommonDTO(fromWarehouseId, fromWarehouseName);
        this.toWarehouse = new CommonDTO(toWarehouseId, toWarehouseName);
    }
}
