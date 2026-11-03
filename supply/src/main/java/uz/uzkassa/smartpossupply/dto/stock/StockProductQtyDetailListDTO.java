package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductQtyDetailListDTO implements Serializable {

    Long id;

    String stockNumber;

    LocalDateTime stockDate;

    LocalDateTime stockCompletedDate;

    BigDecimal qty;

    CompanyBaseDTO company;

    CommonDTO fromWarehouse;

    CommonDTO toWarehouse;

    @SuppressWarnings("unused")
    public StockProductQtyDetailListDTO(Long id, String stockNumber, LocalDateTime stockDate, LocalDateTime stockCompletedDate, BigDecimal qty, Long companyId, String companyName, String companyTin, Long fromWarehouseId, String fromWarehouseName, Long toWarehouseId, String toWarehouseName) {
        this.id = id;
        this.stockNumber = stockNumber;
        this.stockDate = stockDate;
        this.stockCompletedDate = stockCompletedDate;
        this.qty = qty;
        if (companyId != null) {
            this.company = new CompanyBaseDTO(companyId, companyName, companyTin);
        }
        this.fromWarehouse = fromWarehouseId != null ? new CommonDTO(fromWarehouseId, fromWarehouseName) : null;
        this.toWarehouse = toWarehouseId != null ? new CommonDTO(toWarehouseId, toWarehouseName) : null;
    }

    @SuppressWarnings("unused")
    public StockProductQtyDetailListDTO(Long id, String stockNumber, LocalDateTime stockDate, LocalDateTime stockCompletedDate, BigDecimal qty, Long companyId, String companyName, String companyTin) {
        this.id = id;
        this.stockNumber = stockNumber;
        this.stockDate = stockDate;
        this.stockCompletedDate = stockCompletedDate;
        this.qty = qty;
        if (companyId != null) {
            this.company = new CompanyBaseDTO(companyId, companyName, companyTin);
        }
    }
}
