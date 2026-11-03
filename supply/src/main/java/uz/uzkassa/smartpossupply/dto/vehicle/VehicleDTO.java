package uz.uzkassa.smartpossupply.dto.vehicle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.TrailerType;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleDTO implements Serializable {

    Long id;

    @NotNull
    String name;

    Long companyId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String regNo;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String model;

    String trailerRegNo;

    String trailerModel;

    TrailerType trailerType;

    String transportCompanyTin;

    String transportCompanyName;

   @NotNull
    String ownerFullName;

    @NotNull
    String ownerPinfl;
}
