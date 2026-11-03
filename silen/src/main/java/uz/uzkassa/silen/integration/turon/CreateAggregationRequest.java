package uz.uzkassa.silen.integration.turon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAggregationRequest implements Serializable {
    private List<AggregationItem> aggregationUnits;
    private String participantId;
    private String productionLineId;

    public CreateAggregationRequest(List<AggregationItem> aggregationUnits, String participantId) {
        this.aggregationUnits = aggregationUnits;
        this.participantId = participantId;
    }
}
