package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.19.2022
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationUsedStatisticsDTO {

    Long aggregationOrderId;

    Integer used;

    public AggregationUsedStatisticsDTO() {
    }

    public AggregationUsedStatisticsDTO(Long aggregationOrderId, Integer used) {
        this.aggregationOrderId = aggregationOrderId;
        this.used = used;
    }
}
