package uz.uzkassa.cts.dto.onetimeorder.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;

@Getter
@Setter
public class OneTimeOrderChangeStatusRequestDto implements Serializable {
    @NotNull
    private Long id;
    @NotNull
    private ContractStatus status;
}
