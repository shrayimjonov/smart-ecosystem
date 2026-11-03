package uz.uzkassa.smartpossupply.dto.vehicle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleLookupDTO extends CommonDTO {

    String regNo;

    String model;

    String trailerRegNo;

    String trailerModel;

    String ownerFullName;

    String ownerPinfl;

    String transportCompanyTin;

    String transportCompanyName;
}
