package uz.uzkassa.horeca.dto.address;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 457348734L;

    Long regionId;

    Long districtId;

    Long neighborhoodId;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;
}
