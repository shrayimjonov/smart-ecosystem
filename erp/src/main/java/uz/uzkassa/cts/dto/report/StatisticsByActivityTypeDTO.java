package uz.uzkassa.cts.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class StatisticsByActivityTypeDTO implements Serializable {

    Long categoryId;

    String category;

    Long activityTypeId;

    String activityType;

    BigInteger customerTotal;

    Long regionId;

    String region;

    Long districtId;

    String district;

}
