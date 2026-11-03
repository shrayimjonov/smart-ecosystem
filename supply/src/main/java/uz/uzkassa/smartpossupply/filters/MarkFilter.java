package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkFilter extends BaseFilter {

    Long partyId;

    @JsonProperty("used")
    Boolean used;

    Long partyProductId;

    String turonStatus;

    Long aicProductId;
}
