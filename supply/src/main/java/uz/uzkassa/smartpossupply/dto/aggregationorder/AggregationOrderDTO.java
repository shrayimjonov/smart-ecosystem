package uz.uzkassa.smartpossupply.dto.aggregationorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationOrderDTO implements Serializable {

    PackageType packageType;

    Integer qty;

    Long forEmployeeId;
}
