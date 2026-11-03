package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DailyOperationsMaterialDTO {
    private String productId;
    private String productName;
    private String barcode;
    private BigDecimal price;
    private String stockPrice;
    private String stockQty;

    private String serialId;
    private String serialNumber;
    private LocalDate serialProductionDate;

    private BigDecimal producedQty;
    private BigDecimal shippedQty;
    private BigDecimal returnedQty;
    private BigDecimal dropoutQty;

    public DailyOperationsMaterialDTO() {
    }
}
