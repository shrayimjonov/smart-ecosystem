package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReportDTO {
    private Long registerId;
    private String categoryName;
    private String productName;
    private String barcode;
    private String vatBarcode;
    private String unitName;
    private BigDecimal salesPrice;
    private BigDecimal balance;
}
