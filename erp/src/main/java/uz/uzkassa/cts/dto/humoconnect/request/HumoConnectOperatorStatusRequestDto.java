package uz.uzkassa.cts.dto.humoconnect.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.HumoConnectOperatorStatus;

@Getter
@Setter
public class HumoConnectOperatorStatusRequestDto {
    @NotNull
    Long id;

    @NotNull
    HumoConnectOperatorStatus operatorStatus;
}
