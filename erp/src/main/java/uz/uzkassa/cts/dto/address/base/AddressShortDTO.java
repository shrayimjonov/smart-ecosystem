package uz.uzkassa.cts.dto.address.base;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AddressShortDTO implements Serializable {

    CommonDTO region;

    CommonDTO district;

    CommonDTO neighborhood;
}
