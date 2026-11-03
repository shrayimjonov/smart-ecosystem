package uz.uzkassa.smartpossupply.dto.integration.turon.aggregation;

import lombok.*;
import uz.uzkassa.smartpossupply.dto.aggregation.turon.TuronAggregationItem;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TuronCreateAggregationRequest implements Serializable {

    private List<TuronAggregationItem> aggregationUnits;
    private String participantId;
    private String productionLineId;

    public TuronCreateAggregationRequest(List<TuronAggregationItem> aggregationUnits, String participantId) {
        this.aggregationUnits = aggregationUnits;
        this.participantId = participantId;
    }
}
