package uz.uzkassa.smartpossupply.dto.integration.quote;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.BillingContractStatus;

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
