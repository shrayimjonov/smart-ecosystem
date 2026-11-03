package uz.uzkassa.horeca.dto.billing.quote;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuoteCreateRequestDto {
    @NotNull
    private Long agreementId;
    private String note;
}
