package uz.uzkassa.silen.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.invoice.InvoiceBaseDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.PurchaseOrder} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderListDTO implements Serializable {

    Long id;

    String number;

    CustomerCommonDTO customer;

    SelectItem contract;

    EnumDTO status;

    EnumDTO type;

    SelectItem store;

    LocalDate orderDate;

    List<SelectItem> shipments = new ArrayList<>();

    List<InvoiceBaseDTO> invoices = new ArrayList<>();

    String createdBy;
}
