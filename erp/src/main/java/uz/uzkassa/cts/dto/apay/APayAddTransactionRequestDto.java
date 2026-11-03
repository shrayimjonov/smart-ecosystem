package uz.uzkassa.cts.dto.apay;

import lombok.Data;

import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class APayAddTransactionRequestDto implements Serializable {
    private BigDecimal amount;
    private String expiry;
    private String pan;
    private String integrationType = "CTO_BILLING";
    @Size(min = 9, max = 14)
    private String tin;
}
