package uz.uzkassa.cts.dto.onetimeorder.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OneTimeRequestCreatDto implements Serializable {
    @NotNull
    private Long contractId;
    @NotNull
    private Long customerId;
    @NotNull
    private Long serviceId;
    @NotNull
    private boolean calculateWithVat = false;
    private Integer quantity;
    @NotNull
    private Long customerBranchId;
}
