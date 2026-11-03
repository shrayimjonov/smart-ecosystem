package uz.uzkassa.cts.dto.tradeincountercontract.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TradeInCounterContractEditRequestDto{

    @NotNull
    private Long id;

    @NotNull
    private Long tradeInCounterDevicePriceId;

    @NotNull
    private Long tradeInDeviceId;

    @NotNull
    @NotEmpty
    private String deviceSerialNumber;
}

