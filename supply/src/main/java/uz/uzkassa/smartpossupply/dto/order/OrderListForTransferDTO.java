package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.MarketplaceAddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderListForTransferDTO {

    Long id;

    CompanyBaseDTO customer;

    LocalDateTime orderDate;

    OrderStatus status;

    CommonDTO branch;

    CommonDTO warehouse;

    String orderNumber;

    CommonDTO deliveryBranch;

    MarketplaceAddressDetailDTO deliveryAddress;

    LocalDateTime deliveryDate;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    CommonDTO expeditor;

    CommonDTO agent;

    CommonDTO planRoute;
}
