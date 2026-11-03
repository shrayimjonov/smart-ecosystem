package uz.uzkassa.smartpossupply.dto.sync.company;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressSyncDTO implements Serializable {

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
