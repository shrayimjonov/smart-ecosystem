package uz.uzkassa.cts.dto.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTotalByRegionDTO implements Serializable {

    Long regionId;

    String region;

    Long districtId;

    String district;

    BigInteger total;

}
