package uz.uzkassa.smartpos.integration.apay;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class AddressFullDTO {

    RegionDistrictDTO region;

    RegionDistrictDTO district;

    String street;

    String house;

    String office;

    String apartment;

    String longitude;

    String latitude;
}
