package uz.uzkassa.smartpossupply.dto.cashflow;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.CashFlowType;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CashFlowDTO implements Serializable {

    Long customerId;

    BigDecimal amount;

    PaymentType paymentType;

    CurrencyType currencyType;

    String description;

    CashFlowType cashFlowType;

    Long companyId;

    Long stockOutId;
}
