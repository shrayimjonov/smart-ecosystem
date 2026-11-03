package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class APayPaymentQrRequestDto implements Serializable {
    @NotNull
    @Size(min = 9, max = 14)
    private String tin;

    @NotNull
    private BigDecimal amount;
}
