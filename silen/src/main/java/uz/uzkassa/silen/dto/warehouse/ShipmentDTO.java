package uz.uzkassa.silen.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.DocumentType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Shipment} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentDTO implements Serializable {

    String number;

    Long customerId;

    @NotNull
    LocalDateTime shipmentDate;

    Long storeId;

    String storeName;

    boolean isCommittent;

    DocumentType documentType = DocumentType.INVOICE;

    String note;
}
