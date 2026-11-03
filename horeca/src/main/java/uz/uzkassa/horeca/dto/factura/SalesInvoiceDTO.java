package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesInvoiceDTO {

    String productName;
    BigDecimal mestQty;
    String mestUnit;
    BigDecimal qty;
    BigDecimal price;
    BigDecimal totalPrice;
    String unit;
    String artikul;
    BigDecimal decNumber;
    String country;
}
