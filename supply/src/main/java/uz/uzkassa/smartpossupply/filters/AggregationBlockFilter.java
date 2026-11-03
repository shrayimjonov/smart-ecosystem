package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggregationBlockFilter extends BaseFilter {

    Long parentId;

    Long markOrderId;

    Long markOrderPartyId;

    Long aggregationOrderId;

    Long productId;

    String serialId;

    String packageType;

    boolean isBlock = false;

    @JsonProperty("used")
    Boolean used;

    Long partyProductId;

    String turonStatus;
}
