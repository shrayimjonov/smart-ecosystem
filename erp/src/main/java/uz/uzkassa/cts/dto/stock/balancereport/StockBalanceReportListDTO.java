package uz.uzkassa.cts.dto.stock.balancereport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockBalanceReportListDTO implements Serializable {

    Long id;

    CommonDTO item;

    CommonDTO warehouse;

    CommonDTO branch;

    CommonDTO company;

    BigDecimal qty;

    @SuppressWarnings("unused")
    public StockBalanceReportListDTO(Long itemId, String itemName, Long warehouseId, String warehouseName, Long branchId, String branchName, Long companyId, String companyName, BigDecimal qty) {
        this.id = new Random().nextLong(1,10000);
        this.item = new CommonDTO(itemId, itemName);
        this.warehouse = new CommonDTO(warehouseId, warehouseName);
        this.branch = new CommonDTO(branchId, branchName);
        this.company = new CommonDTO(companyId, companyName);
        this.qty = qty;
    }
}
