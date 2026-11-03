package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDTO implements Serializable {

    Long regionId;
    Long regionCode;
    String regionName;

    Long districtId;
    Long districtCode;
    String districtName;


    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;
}
