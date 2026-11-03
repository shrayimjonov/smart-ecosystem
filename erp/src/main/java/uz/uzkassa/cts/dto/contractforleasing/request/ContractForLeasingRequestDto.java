package uz.uzkassa.cts.dto.contractforleasing.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractForLeasingRequestDto {
    @NotNull
    private Long modelPriceHistoryId;
    @NotNull
    private Long customerId;
    @NotNull
    private Integer totalProductCount;
}
