package uz.uzkassa.smartpossupply.dto.order.onec;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.OrderSource;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order1CListDTO implements Serializable {

    Long id;

    CompanyBaseDTO customer;

    LocalDateTime orderDate;

    OrderStatus status;

    String orderNumber;

    OrderType orderType;

    CommonDTO branch;

    CommonDTO vehicle;

    CommonDTO planRoute;

    LocalDateTime createdDate;

    OrderSource source;
}
