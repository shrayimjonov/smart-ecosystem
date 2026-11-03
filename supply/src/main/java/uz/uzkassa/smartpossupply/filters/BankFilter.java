package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankFilter extends BaseFilter {

    @JsonProperty("fetchOnlyParent")
    boolean fetchOnlyParent;
}
