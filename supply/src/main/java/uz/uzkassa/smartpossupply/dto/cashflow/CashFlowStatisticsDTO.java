package uz.uzkassa.smartpossupply.dto.cashflow;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CashFlowStatisticsDTO {

    Long totalNumberOfOperations;

    BigDecimal sumAllOperations;

    BigDecimal sumCashOperations;

    BigDecimal sumCardOperations;

    BigDecimal sumTransferOperations;

    public CashFlowStatisticsDTO(Long totalNumberOfOperations, BigDecimal sumAllOperations, BigDecimal sumCashOperations, BigDecimal sumCardOperations, BigDecimal sumTransferOperations) {
        this.totalNumberOfOperations = totalNumberOfOperations;
        this.sumAllOperations = sumAllOperations;
        this.sumCashOperations = sumCashOperations;
        this.sumCardOperations = sumCardOperations;
        this.sumTransferOperations = sumTransferOperations;
    }
}
