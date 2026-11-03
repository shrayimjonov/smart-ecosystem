package uz.uzkassa.cts.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ActivityTypeFilter extends BaseFilter {

    @JsonProperty("onlyParents")
    Boolean onlyParents;
}
