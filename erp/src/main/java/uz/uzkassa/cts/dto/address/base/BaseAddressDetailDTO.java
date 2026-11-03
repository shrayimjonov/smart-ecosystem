package uz.uzkassa.cts.dto.address.base;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseAddressDetailDTO {

    CommonDTO region;

    CommonDTO district;

    CommonDTO neighborhood;

    String landmark;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;
}
