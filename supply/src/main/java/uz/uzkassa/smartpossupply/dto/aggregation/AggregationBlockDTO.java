package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationBlockDTO implements Serializable {

    Long productId;

    Long childProductId;

    Integer qty;

    String printCode;

    String description;

    PackageType packageType;

    Set<String> marks = new HashSet<>();

    String serialId;

    Long partyProductId;
}
