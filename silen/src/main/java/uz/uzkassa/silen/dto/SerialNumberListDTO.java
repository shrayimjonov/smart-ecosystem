package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.SerialNumberStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SerialNumberListDTO {

    String id;

    String serialNumber;

    String productId;

    String productName;

    String barcode;

    Integer startQuantity;

    Integer quantity;

    Integer forExpertise;

    LocalDateTime createdDate;

    LocalDate productionDate;

    LocalDateTime lastModifiedDate;

    SerialNumberStatus status;

    String statusName;

    String description;

    public SerialNumberListDTO() {
    }

    public SerialNumberListDTO(String id, String serialNumber, String productId, String productName, String barcode,
                               Integer startQuantity, Integer quantity, Integer forExpertise, LocalDateTime createdDate,
                               LocalDate productionDate, LocalDateTime lastModifiedDate, String status, String description) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.startQuantity = startQuantity;
        this.quantity = quantity;
        this.forExpertise = forExpertise;
        this.createdDate = createdDate;
        this.productionDate = productionDate;
        this.lastModifiedDate = lastModifiedDate;
        this.status = SerialNumberStatus.valueOf(status);
        this.statusName = this.status.getNameRu();
        this.description = description;
    }
}
