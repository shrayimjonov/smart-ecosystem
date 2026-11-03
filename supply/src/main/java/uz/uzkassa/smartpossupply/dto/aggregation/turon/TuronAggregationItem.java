package uz.uzkassa.smartpossupply.dto.aggregation.turon;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuronAggregationItem implements Serializable {

    Integer aggregatedItemsCount;

    Integer aggregationUnitCapacity;

    TuronAggregationType aggregationType;

    String unitSerialNumber;

    List<String> sntins;

    public TuronAggregationItem(Integer aggregatedItemsCount, TuronAggregationType aggregationType, String unitSerialNumber, List<String> sntins) {
        this.aggregatedItemsCount = aggregatedItemsCount;
        this.aggregationUnitCapacity = aggregatedItemsCount;
        this.aggregationType = aggregationType;
        this.unitSerialNumber = unitSerialNumber;
        this.sntins = sntins;
    }
}
