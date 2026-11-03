package uz.uzkassa.cts.dto.kapitalbank.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CreatePaymentRequestDto implements Serializable {
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
    @NotEmpty
    @NotBlank
    private String deviceSerialNumber;

    @NotNull
    private BigDecimal amount;

    @Override
    public String toString() {
        return "CreatePaymentRequestDto{" +
                "tin='" + tin + '\'' +
                ", pan='" + pan + '\'' +
                ", expiry='" + expiry + '\'' +
                ", amount=" + amount +
                '}';
    }
}
