package uz.uzkassa.smartpossupply.dto.cashflow;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CashFlowStatus;
import uz.uzkassa.smartpossupply.enums.CashFlowType;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CashFlowListDTO implements Serializable {

    Long id;

    String cashFlowNumber;

    CommonDTO customer;

    BigDecimal amount;

    CashFlowStatus status;

    CurrencyType currencyType;

    PaymentType paymentType;

    CommonCodeDTO stockOut;

    CommonDTO factura;

    String description;

    LocalDateTime createdDate;

    CommonDTO creator;

    CashFlowType cashFlowType;
}
