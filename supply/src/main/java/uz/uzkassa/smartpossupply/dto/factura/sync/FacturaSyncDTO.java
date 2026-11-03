package uz.uzkassa.smartpossupply.dto.factura.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.InvoiceType;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaSyncDTO implements Serializable {

    LocalDate fromDate;

    LocalDate toDate;

    InvoiceType invoiceType;

    String facturaId;
}
