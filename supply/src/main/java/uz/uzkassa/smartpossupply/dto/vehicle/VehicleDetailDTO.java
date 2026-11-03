package uz.uzkassa.smartpossupply.dto.vehicle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.TrailerType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleDetailDTO extends CommonDTO {

    CommonDTO company;

    CommonStatus status;

    String regNo;

    String model;

    String trailerRegNo;

    String trailerModel;

    TrailerType trailerType;

    String transportCompanyTin;

    String transportCompanyName;

    String ownerPinfl;

    String ownerFullName;
}
