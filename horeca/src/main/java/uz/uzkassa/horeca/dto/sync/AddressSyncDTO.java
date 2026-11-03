package uz.uzkassa.horeca.dto.sync;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressSyncDTO {

    Long regionCode;

    Long districtCode;

    Integer neighborhoodCode;

    String landmark;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;
}
