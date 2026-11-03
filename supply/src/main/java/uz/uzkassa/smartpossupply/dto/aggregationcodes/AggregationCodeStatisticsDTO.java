package uz.uzkassa.smartpossupply.dto.aggregationcodes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Created time: 20.05.2025 15:59
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationCodeStatisticsDTO {

    Long stockProductAggregationTotal;

    Long stockProductAggregationBlockTotal;

    Long stockProductMarkTotal;
}
