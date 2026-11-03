package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.AggregationOrderStatus;
import uz.uzkassa.smartpossupply.enums.PackageType;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationOrderFilter extends SimpleFilter {
    PackageType packageType;
    AggregationOrderStatus status;
    Long forEmployee;
}
