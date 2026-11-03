package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.19.2022
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationStatisticsDTO {

    Long draftTotal;

    Long errorTotal;

    Long emptyTotal;

    Long createdTotal;

    Long utilizedTotal;

    Long returnedTotal;

    Long shippedTotal;

    public AggregationStatisticsDTO(
        Long draftTotal,
        Long errorTotal,
        Long emptyTotal,
        Long createdTotal,
        Long utilizedTotal,
        Long returnedTotal,
        Long shippedTotal
    ) {
        this.draftTotal = draftTotal;
        this.errorTotal = errorTotal;
        this.emptyTotal = emptyTotal;
        this.createdTotal = createdTotal;
        this.utilizedTotal = utilizedTotal;
        this.returnedTotal = returnedTotal;
        this.shippedTotal = shippedTotal;
    }
}
