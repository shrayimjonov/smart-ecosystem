package uz.uzkassa.cts.dto.integration.billing;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.BillingContractStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteChangeStatusRequestDto {
    @NotNull
    private String id;
    @NotNull
    private BillingContractStatus contractStatus;
    private String tin;
}
