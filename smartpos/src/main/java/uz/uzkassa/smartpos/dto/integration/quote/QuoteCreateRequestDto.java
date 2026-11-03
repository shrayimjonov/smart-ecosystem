package uz.uzkassa.smartpos.dto.integration.quote;

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
