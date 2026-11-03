package uz.uzkassa.horeca.dto.apay;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class APayPaymentAddRequestDto implements Serializable {

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 9, max = 14)
    private String tin;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 16, max = 16)
    private String pan;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 4, max = 4)
    private String expiry;

    @NotNull
    private BigDecimal amount;
}
