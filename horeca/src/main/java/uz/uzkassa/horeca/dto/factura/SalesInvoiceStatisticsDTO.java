package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesInvoiceStatisticsDTO {

    BigDecimal factureNumber;
    LocalDate facturaDate;
    String seller;
    String buyer;

    BigDecimal totalProducts;
    BigDecimal totalPrice;
    BigDecimal totalVatPrice;
    //    String totalPriceText;
}
