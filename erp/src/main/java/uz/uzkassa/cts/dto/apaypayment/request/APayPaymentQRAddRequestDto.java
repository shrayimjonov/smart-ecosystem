package uz.uzkassa.cts.dto.apaypayment.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class APayPaymentQRAddRequestDto {
    @NotEmpty
    @Size(min = 9, max = 14)
    private String tin;

    @NotNull
    private BigDecimal amount;
}

