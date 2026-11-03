package uz.uzkassa.cts.dto.tin.integration;

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
public class OfdChangeAddressRequestDTO implements Serializable {

    Long applicationId;

    Long districtCode;

    Long regionCode;

    Integer neighborhoodCode;

    String address;

    String longitude;

    String latitude;

    Long officeId;
}
