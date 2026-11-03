package uz.uzkassa.cts.dto.ledger.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LedgerTinPinflResponseDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal debit;
    private BigDecimal credit;
    private String customerName;
    private LocalDate paymentDate;
    private String tin;
    private String description;
    private String referenceTo;
    private Long customerId;

}
