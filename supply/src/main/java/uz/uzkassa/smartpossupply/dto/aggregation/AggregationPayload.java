package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.util.LinkedHashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationPayload {

    Long productId;

    ProductType productType;

    Long childProductId;

    Integer qty;

    String printCode;

    PackageType packageType;

    Set<String> marks = new LinkedHashSet<>();

    Long partyProductId;

    boolean sendToTuron;
}
