package uz.uzkassa.smartpossupply.dto.financialoperation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 09.01.2024
 */
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinancialOperationStatisticsDTO {

    Long totalNumberOfOperations;

    BigDecimal sumAllOperations;

    BigDecimal sumCashOperations;

    BigDecimal sumCardOperations;

    BigDecimal sumTransferOperations;

    BigDecimal sumDebitOperations;

    BigDecimal sumCreditOperations;

    BigDecimal totalSum;

    public FinancialOperationStatisticsDTO(Long totalNumberOfOperations, BigDecimal sumAllOperations, BigDecimal sumCashOperations, BigDecimal sumCardOperations, BigDecimal sumTransferOperations, BigDecimal sumDebitOperations, BigDecimal sumCreditOperations) {
        this.totalNumberOfOperations = totalNumberOfOperations;
        this.sumAllOperations = sumAllOperations;
        this.sumCashOperations = sumCashOperations;
        this.sumCardOperations = sumCardOperations;
        this.sumTransferOperations = sumTransferOperations;
        this.sumDebitOperations = sumDebitOperations;
        this.sumCreditOperations = sumCreditOperations;
        this.totalSum = Optional.ofNullable(sumDebitOperations).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(sumCreditOperations).orElse(BigDecimal.ZERO));
    }
}
