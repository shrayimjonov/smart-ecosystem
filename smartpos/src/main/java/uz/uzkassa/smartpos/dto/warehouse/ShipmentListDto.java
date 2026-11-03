package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.ShipmentStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Shipment}
 */
@Getter
@Setter
public class ShipmentListDto implements Serializable {
    private String createdBy;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate = LocalDateTime.now();
    private Long id;
    @NotBlank
    private String number;
    private int intNumber;
    private LocalDateTime shipmentDate;
    @NotNull
    private ShipmentStatus status = ShipmentStatus.NEW;
    @NotNull
    private Long companyId;
    @NotBlank
    private String customerTin;
    @NotBlank
    private String customerName;
    private String description;
    private CommonDTO invoice;
}
