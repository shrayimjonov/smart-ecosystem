package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceSelectItemDTO implements Serializable {
    private String facturaId;
    private String number;
    private LocalDate invoiceDate;
    private BigDecimal total;
}
