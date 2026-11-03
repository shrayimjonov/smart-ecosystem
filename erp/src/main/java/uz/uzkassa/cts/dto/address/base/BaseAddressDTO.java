package uz.uzkassa.cts.dto.address.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BaseAddressDTO implements Serializable {

    Long regionId;

    Long districtId;

    Long neighborhoodId;

    String landmark;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;
}
