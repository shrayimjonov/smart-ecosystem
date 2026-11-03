package uz.uzkassa.cts.dto.rentDeviceAct;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class RentDeviceActPayload implements Serializable {
    @NotNull
    private Long customerId;
    @NotNull
    private Long deviceId;

    public RentDeviceActPayload(Long customerId, Long deviceId) {
        this.customerId = customerId;
        this.deviceId = deviceId;
    }
}
