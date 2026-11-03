package uz.uzkassa.cts.dto.apay;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.address.BaseAddress;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class ApayAddressDTO {

    ApayLocalizedDTO region;

    ApayLocalizedDTO district;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;

    public ApayAddressDTO(BaseAddress address) {
        if (address.getRegion() != null) {
            this.region = new ApayLocalizedDTO(address.getRegion().getId().toString(), address.getRegion().getNameRu(), address.getRegion().getNameUzLatin(), address.getRegion().getCode());
        }
        if (address.getDistrict() != null) {
            this.district = new ApayLocalizedDTO(address.getDistrict().getId().toString(), address.getDistrict().getNameRu(), address.getDistrict().getNameUzLatin(), address.getDistrict().getCode());
        }
        this.street = address.getStreet();
        this.house = address.getHouse();
        this.apartment = address.getApartment();
        this.longitude = address.getLongitude();
        this.latitude = address.getLatitude();
    }
}
