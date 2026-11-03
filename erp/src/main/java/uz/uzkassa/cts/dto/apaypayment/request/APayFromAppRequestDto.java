package uz.uzkassa.cts.dto.apaypayment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Data
public class APayFromAppRequestDto implements Serializable {
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

    private String billId;
    private String maskedPhoneNumber;
    private APayStatus addStatus;
    private LocalDateTime addTime;
    private String addErrorMessage;
    private APayStatus payStatus;
    private LocalDateTime payTime;
    private String payErrorMessage;
}
