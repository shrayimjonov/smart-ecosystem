package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggregationFilter extends BaseFilter {

    Long aggregationOrderId;

    String packageType;

    Long markOrderPartyId;

    boolean isBlock = false;

    @JsonProperty("used")
    Boolean used;

    Long partyProductId;

    Long productSerialId;
}
