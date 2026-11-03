package uz.uzkassa.smartpossupply.dto.address;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class MarketplaceAddressDetailDTO {

    CommonCodeDTO region;

    CommonCodeDTO district;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;

    Long regionCode;

    Long districtCode;

    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (getRegion() != null) {
            address.append(getRegion().getName()).append(" ");
        }
        if (getDistrict() != null) {
            address.append(getDistrict().getName()).append(" ");
        }
        if (getStreet() != null) {
            address.append(getStreet()).append(" ");
        }
        if (getHouse() != null) {
            address.append(getHouse()).append(" ");
        }
        if (getApartment() != null) {
            address.append(getApartment()).append(" ");
        }

        return address.toString();
    }
}
