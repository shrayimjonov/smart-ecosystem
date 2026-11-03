package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.PurchaseOrder} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderDTO implements Serializable {

    Long id;

    String number;

    @NotNull
    Long customerId;

    SelectItem customer;

    String contractId;

    SelectItem contract;

    String status;

    String statusName;

    Long storeId;

    SelectItem store;

    LocalDate orderDate;

    String fromOrganizationId;

    String createdBy;

}
