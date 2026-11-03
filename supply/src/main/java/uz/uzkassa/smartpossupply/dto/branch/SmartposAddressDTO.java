package uz.uzkassa.smartpossupply.dto.branch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.08.2024
 */

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmartposAddressDTO implements Serializable {

    CommonCodeDTO region;

    CommonCodeDTO district;

    String house;

    String apartment;

    String longitude;

    String latitude;
}
