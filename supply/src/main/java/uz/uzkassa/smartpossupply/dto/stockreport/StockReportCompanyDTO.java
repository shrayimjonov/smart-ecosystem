package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StockReportCompanyDTO {
    Long customerId;
    String customerName;
    BigDecimal totalQty;
}
