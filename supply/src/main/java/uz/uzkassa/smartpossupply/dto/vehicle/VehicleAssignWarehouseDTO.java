package uz.uzkassa.smartpossupply.dto.vehicle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleAssignWarehouseDTO implements Serializable {

    List<Long> warehouseIds;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Long vehicleId;

    boolean addAll;
}
