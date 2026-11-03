package uz.uzkassa.cts.dto.ledger.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncomeExpenseResponseDto {
    private BigDecimal credit;
    private BigDecimal debit;

    public IncomeExpenseResponseDto(BigDecimal credit, BigDecimal debit) {
        this.credit = credit;
        this.debit = debit;
    }

    public IncomeExpenseResponseDto() {
    }
}
