package uz.uzkassa.cts.dto.invoice.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class LedgerResponseDto implements Serializable {


    private Long id;

    @NotNull
    private String referenceTo;

    @NotNull
    private String description;

    @NotNull
    private Long transactionId;

    private String customerName;

    private String accountNumber;

    @NotNull
    private BigDecimal debit;

    @NotNull
    private BigDecimal credit;

    @NotNull
    private BigDecimal balance;
}
