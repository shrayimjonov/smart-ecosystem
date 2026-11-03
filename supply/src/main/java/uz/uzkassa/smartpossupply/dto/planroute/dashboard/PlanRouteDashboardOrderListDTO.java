package uz.uzkassa.smartpossupply.dto.planroute.dashboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteDashboardOrderListDTO {

    Long id;

    String orderNumber;

    LocalDateTime orderDate;

    OrderStatus status;

    String description;

    CommonDTO agent;
}
