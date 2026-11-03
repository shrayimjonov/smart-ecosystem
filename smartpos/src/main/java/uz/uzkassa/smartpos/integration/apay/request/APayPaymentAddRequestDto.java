package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class APayPaymentAddRequestDto implements Serializable {
    @NotNull
    @Size(min = 9, max = 14)
    private String tin;

    @NotNull
    @Size(min = 16, max = 16)
    private String pan;

    @NotNull
    @Size(min = 4, max = 4)
    private String expiry;

    @NotNull
    private BigDecimal amount;
}
