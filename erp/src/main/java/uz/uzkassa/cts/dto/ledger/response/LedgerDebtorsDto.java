package uz.uzkassa.cts.dto.ledger.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class LedgerDebtorsDto {
    private BigDecimal balance;
    private BigInteger count;

    public LedgerDebtorsDto(BigDecimal balance, BigInteger count) {
        this.balance = balance;
        this.count = count;
    }

    public LedgerDebtorsDto() {
    }

}
