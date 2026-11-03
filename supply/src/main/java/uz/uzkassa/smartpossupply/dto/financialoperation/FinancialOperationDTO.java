package uz.uzkassa.smartpossupply.dto.financialoperation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 09.01.2024
 */
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinancialOperationDTO implements Serializable {

    Long id;

    Long customerId;

    Long customerBranchId;

    BigDecimal amount;

    BigDecimal oldAmount;

    PaymentType paymentType;

    String description;

    Long stockOutId;

    Long facturaId;

    @JsonIgnore
    FinancialOperationType operationType;

    LocalDateTime paymentReceivedDate;

    BigDecimal foreignPrice;

    CurrencyType currencyType;

    BigDecimal exchangeRate;
}
