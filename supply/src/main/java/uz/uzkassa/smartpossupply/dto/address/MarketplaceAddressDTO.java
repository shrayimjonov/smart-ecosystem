package uz.uzkassa.smartpossupply.dto.address;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceAddressDTO extends AddressDTO {

    Long regionCode;

    Long districtCode;
}
