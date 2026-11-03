package uz.uzkassa.silen.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.invoice.InvoiceBaseDTO;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.enumeration.TransferType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInListDTO {

    String id;

    String number;

    SelectItem customer;

    String tin;

    SelectItem store;

    List<InvoiceBaseDTO> invoices;

    TransferType transferType;

    StockInStatus status;

    SelectItem organization;

    LocalDateTime transferDate;

    SelectItem shipment;

    String login;
}
