package uz.uzkassa.smartpossupply.dto.order;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OrderReportDTO {

    Long id;

    Long branchId;

    String branchName;

    Long agentId;

    String agentName;

    OrderStatus status;

    BigDecimal totalQty;

    Long productCount;

    BigDecimal packageCount;

    BigDecimal productCountInPackage;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    BigDecimal totalPrice;

    PaymentType paymentType;

    List<CommonDTO> products;
}
