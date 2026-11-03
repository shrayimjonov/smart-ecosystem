package uz.uzkassa.cts.dto.tradeincontract.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeInDeviceAddRequestDto {
    @NotNull
    private Long deviceId;
    private boolean skipValidation = false;

    public TradeInDeviceAddRequestDto(@NotNull Long deviceId) {
        this.deviceId = deviceId;
    }
}
