package uz.uzkassa.silen.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.PurchaseOrder} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderDetailDTO implements Serializable {

    Long id;

    String number;

    SelectItem customer;

    SelectItem contract;

    EnumDTO status;

    EnumDTO type;

    SelectItem store;

    LocalDate orderDate;

    String createdBy;

    List<SelectItem> shipments;

    Set<PurchaseOrderProductDetailDTO> products;
}
