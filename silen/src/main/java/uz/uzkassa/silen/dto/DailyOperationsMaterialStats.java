package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.domain.SerialNumber;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DailyOperationsMaterialStats {
    private String productId;
    private String productName;
    private String barcode;
    private BigDecimal price;
    private String serialId;
    private String serialNumber;
    private LocalDate serialProductionDate;
    private BigDecimal productionQty;
    private BigDecimal shippedQty;
    private BigDecimal returnedQty;
    private BigDecimal dropoutQty;


    private BigDecimal startPrice;
    private BigDecimal startAmount;

    private BigDecimal endPrice;
    private BigDecimal endAmount;

    public DailyOperationsMaterialStats() {
    }

    public DailyOperationsMaterialStats(String productId, String productName, String barcode, BigDecimal price,
                                        String serialId, String serialNumber, LocalDate serialProductionDate,
                                        BigDecimal productionQty, BigDecimal shippedQty, BigDecimal returnedQty, BigDecimal dropoutQty) {
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.price = price;
        this.serialId = serialId;
        this.serialProductionDate = serialProductionDate;
        this.productionQty = productionQty;
        this.shippedQty = shippedQty;
        this.returnedQty = returnedQty;
        this.dropoutQty = dropoutQty;
        if (serialProductionDate != null && serialNumber != null) {
            this.serialNumber = SerialNumber.getNameWithDate(serialNumber, serialProductionDate);
        }
    }
}
