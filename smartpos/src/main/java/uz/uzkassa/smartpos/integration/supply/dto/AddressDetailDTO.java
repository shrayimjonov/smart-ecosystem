package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class AddressDetailDTO {

    CommonDTO region;

    CommonDTO district;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;

}
