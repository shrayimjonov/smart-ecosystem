package uz.uzkassa.cts.dto.humoconnect.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.HumoConnectStatus;

@Getter
@Setter
public class HumoConnectStatusRequestDto {
    @NotNull
    Long id;

    @NotNull
    HumoConnectStatus status;
}
