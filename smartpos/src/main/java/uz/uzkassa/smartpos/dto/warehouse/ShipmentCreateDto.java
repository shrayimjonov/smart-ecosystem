package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Shipment;
import uz.uzkassa.smartpos.enums.ShipmentStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Shipment}
 */
@Getter
@Setter
public class ShipmentCreateDto implements Serializable {
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
    @NotEmpty
    @Valid
    private List<ShipmentItemCreateDto> items = new ArrayList<>();
}
