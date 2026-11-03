package uz.uzkassa.cts.dto.ledger.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LedgerDto {

    private Long id;

    @NotNull
    private String accountNumber;

    @NotNull
    private String referenceTo;

    @NotNull
    private String customerName;


    @NotNull
    private String description;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String tin;

}
