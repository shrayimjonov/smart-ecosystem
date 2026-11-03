package uz.uzkassa.cts.dto.ledger.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class LedBalanceDto {
    public LedBalanceDto(Long id, String account_number, BigDecimal balance, BigDecimal credit, String customer_name, BigDecimal debit, String link, String payment_date, String tin, String transaction_number, String service_type) {
        this.id = id;
        this.account_number = account_number;
        this.balance = balance;
        this.credit = credit;
        this.customer_name = customer_name;
        this.debit = debit;
        this.link = link;
        this.payment_date = payment_date;
        this.tin = tin;
        this.transaction_number = transaction_number;
        this.service_type = service_type;
    }

    private Long id;
    private String account_number;
    private BigDecimal balance;
    private BigDecimal credit;
    private String customer_name;
    private BigDecimal debit;
    private String link;
    private String payment_date;
    private String tin;
    private String transaction_number;
    private String service_type;
}
