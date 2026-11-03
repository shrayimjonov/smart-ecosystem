package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentOrderFilter extends SimpleFilter {
    Long planRouteId;
}
