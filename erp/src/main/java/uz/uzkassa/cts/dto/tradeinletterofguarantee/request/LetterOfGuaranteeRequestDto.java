package uz.uzkassa.cts.dto.tradeinletterofguarantee.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetterOfGuaranteeRequestDto {
    @NotNull
    private Long customerId;
    @NotNull
    private Long tradeInDeviceId;
    @NotNull
    @NotEmpty
    private String deviceSerialNumber;
}
