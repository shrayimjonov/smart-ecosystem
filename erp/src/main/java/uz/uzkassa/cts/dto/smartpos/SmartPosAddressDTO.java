package uz.uzkassa.cts.dto.smartpos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SmartPosAddressDTO implements Serializable {

    Long regionCode;

    Long districtCode;

    Long neighborCode;

    String neighborName;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;

    public SmartPosAddressDTO(Long regionCode, Long districtCode, String street, String house, String apartment, String longitude, String latitude) {
        this.regionCode = regionCode;
        this.districtCode = districtCode;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
