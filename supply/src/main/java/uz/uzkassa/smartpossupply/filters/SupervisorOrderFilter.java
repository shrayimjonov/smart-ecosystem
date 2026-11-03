package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupervisorOrderFilter extends SimpleFilter {
    Long supervisorId;
    Set<Long> agentIds;
    Long planRouteId;
    String status;
    Long customerId;
    Set<Long> expeditorIds;
    LocalDateTime deliveryFromDate;
    LocalDateTime deliveryToDate;
}
