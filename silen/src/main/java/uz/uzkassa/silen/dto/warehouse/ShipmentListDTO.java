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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Order} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentListDTO implements Serializable {

    String id;

    String number;

    String branchCode;

    SelectItem customer;

    LocalDateTime shipmentDate;

    String statusName;

    SelectItem store;

    EnumDTO status;

    Set<InvoiceBaseDTO> invoices = new HashSet<>();

    Set<SelectItem> acts = new HashSet<>();

    SelectItem purchaseOrders;

    boolean isComitent;

    String note;
}
