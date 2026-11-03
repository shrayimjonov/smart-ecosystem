package uz.uzkassa.silen.integration.turon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationType;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationItem implements Serializable {

    Integer aggregatedItemsCount;

    Integer aggregationUnitCapacity;

    AggregationType aggregationType;

    String unitSerialNumber;

    Collection<String> sntins;
}
