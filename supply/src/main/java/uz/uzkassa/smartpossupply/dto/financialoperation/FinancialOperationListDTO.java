package uz.uzkassa.smartpossupply.dto.financialoperation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
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
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinancialOperationListDTO implements Serializable {

    Long id;

    String operationNumber;

    CompanyBaseDTO customer;

    CommonDTO customerBranch;

    BigDecimal amount;

    PaymentType paymentType;

    CommonStatus status;

    CommonCodeDTO stockOut;

    CommonDTO factura;

    String description;

    LocalDateTime createdDate;

    CommonDTO creator;

    FinancialOperationType operationType;

    LocalDateTime paymentReceivedDate;

    BigDecimal foreignPrice;

    CurrencyType currencyType;

    BigDecimal exchangeRate;
}
