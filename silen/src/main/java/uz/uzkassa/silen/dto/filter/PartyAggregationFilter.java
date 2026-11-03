package uz.uzkassa.silen.dto.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.PartyAggregationStatus;

@Getter
@Setter

public class PartyAggregationFilter extends Filter {

    private String partyId;

    private PartyAggregationStatus status;
}
