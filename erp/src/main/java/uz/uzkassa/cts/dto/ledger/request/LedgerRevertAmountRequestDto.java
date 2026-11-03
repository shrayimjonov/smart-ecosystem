package uz.uzkassa.cts.dto.ledger.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class LedgerRevertAmountRequestDto implements Serializable {
    private BigDecimal amount;
    private String description;
    private String tin;
}
