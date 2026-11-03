package uz.uzkassa.smartpossupply.dto.reconciliationact;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FinancialOperationACTDTO {
    Long id;
    LocalDateTime paymentReceivedDate;
    String operationNumber;
    CommonDTO customerBranch;
    FinancialOperationType operationType;
    CurrencyType currencyType;
    BigDecimal amount;
    BigDecimal foreignPrice;
    Long stockOutId;
}
