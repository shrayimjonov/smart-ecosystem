package uz.uzkassa.horeca.dto.billing.quote;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.filters.billing.BillingContractStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteChangeStatusRequestDto {

    @NotNull
    private String id;

    @NotNull
    private BillingContractStatus contractStatus;
}
