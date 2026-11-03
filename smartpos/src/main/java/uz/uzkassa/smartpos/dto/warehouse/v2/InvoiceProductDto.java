package uz.uzkassa.smartpos.dto.warehouse.v2;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceProductDto {
    private String name;
    private BigDecimal hasExcise;
    private BigDecimal exciseAmount;
    private String barcode;
    private String vatBarcode;
    private BigDecimal vatRate;
}
