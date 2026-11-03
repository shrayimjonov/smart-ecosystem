package uz.uzkassa.cts.dto.ledger.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LedgerBalanceDto {

    public LedgerBalanceDto(Long serId,
                            String serTransactionNumber, String serTin, String serLink,
                            String serPaymentDate, String serCustomerName, String serAccountNumber,
                            BigDecimal serDebit, BigDecimal serCredit, BigDecimal serBalance,
                            String serServiceType) {

        this.serId = serId;
        this.serTransactionNumber = serTransactionNumber;
        this.serTin = serTin;
        this.serLink = serLink;
        this.serPaymentDate = serPaymentDate;
        this.serCustomerName = serCustomerName;
        this.serAccountNumber = serAccountNumber;
        this.serDebit = serDebit;
        this.serCredit = serCredit;
        this.serBalance = serBalance;
        this.serServiceType = serServiceType;
    }

    private Long serId;
    private String serTransactionNumber;
    private String serTin;
    private String serLink;
    private String serPaymentDate;
    private String serCustomerName;
    private String serAccountNumber;
    private BigDecimal serDebit;
    private BigDecimal serCredit;
    private BigDecimal serBalance;
    private String serServiceType;


}
