package uz.uzkassa.cts.dto.ledger.response;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LedgerResponseDto {

    private Long id;

    @NotNull
    private String referenceTo;

    @NotNull
    private String description;

    @NotNull
    private Long transactionId;

    @NotNull
    private String customerName;

    @NotNull
    private String accountNumber;

    @NotNull
    private BigDecimal debit;

    @NotNull
    private BigDecimal credit;

    @NotNull
    private BigDecimal balance;

    private ServiceType serviceType;

    private LocalDateTime createdDate;
}
