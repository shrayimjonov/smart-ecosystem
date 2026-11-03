package uz.uzkassa.smartpos.integration.cto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.enums.LeadPaymentType;

@Getter
@Setter
public class LeadRequestDto {
    @NotNull
    private String tin;
    private String comment;
    @NotNull
    private LeadPaymentType leadPaymentType;
}
