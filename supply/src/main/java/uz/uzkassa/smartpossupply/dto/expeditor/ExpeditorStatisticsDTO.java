package uz.uzkassa.smartpossupply.dto.expeditor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorStatisticsDTO implements Serializable {

    Long expeditorTotal;

    Long planRouteTotal;

    Long orderTotal;

    Long completedOrderTotal;
}
