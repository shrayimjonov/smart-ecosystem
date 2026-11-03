package uz.uzkassa.horeca.dto.address;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class AddressDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 97453355L;

    CommonDTO region;

    CommonDTO district;

    CommonDTO neighborhood;

    String street;

    String house;

    String apartment;

    Double longitude;

    Double latitude;
}
