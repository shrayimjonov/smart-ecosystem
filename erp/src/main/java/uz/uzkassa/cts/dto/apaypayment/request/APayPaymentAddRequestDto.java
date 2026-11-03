package uz.uzkassa.cts.dto.apaypayment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
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
    @NotEmpty
    @NotBlank
    private String deviceSerialNumber;

    @NotNull
    private BigDecimal amount;

    public APayPaymentAddRequestDto(String tin, String pan, String expiry, String deviceSerialNumber, BigDecimal amount) {
        this.tin = tin;
        this.pan = pan;
        this.expiry = expiry;
        this.deviceSerialNumber = deviceSerialNumber;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "APayPaymentAddRequestDto{" +
            "tin='" + tin + '\'' +
            ", pan='" + pan + '\'' +
            ", expiry='" + expiry + '\'' +
            ", deviceSerialNumber='" + deviceSerialNumber + '\'' +
            ", amount=" + amount +
            '}';
    }
}
