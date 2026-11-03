package uz.uzkassa.smartpossupply.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferProductQtyDetailListDTO extends StockProductQtyDetailListDTO {

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    @SuppressWarnings("unused")
    public StockTransferProductQtyDetailListDTO(Long id, String stockNumber, LocalDateTime stockDate, LocalDateTime stockCompletedDate, BigDecimal qty, Long companyId, String companyName, String companyTin, Long fromWarehouseId, String fromWarehouseName, Long toWarehouseId, String toWarehouseName) {
        super(id, stockNumber, stockDate, stockCompletedDate, qty, companyId, companyName, companyTin, fromWarehouseId, fromWarehouseName, toWarehouseId, toWarehouseName);
        this.fromWarehouse = fromWarehouseId != null ? new CommonDTO(fromWarehouseId, fromWarehouseName) : null;
        this.toWarehouse = toWarehouseId != null ? new CommonDTO(toWarehouseId, toWarehouseName) : null;
    }
}
