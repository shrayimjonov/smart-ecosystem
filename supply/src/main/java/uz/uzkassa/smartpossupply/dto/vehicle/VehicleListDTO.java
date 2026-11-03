package uz.uzkassa.smartpossupply.dto.vehicle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleListDTO extends CommonDTO {

    String ownerFullName;

    String ownerPinfl;

    String regNo;

    String model;

    CommonStatus status;
}
