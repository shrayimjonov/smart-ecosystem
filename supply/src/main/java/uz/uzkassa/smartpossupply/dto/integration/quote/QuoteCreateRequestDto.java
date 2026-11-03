package uz.uzkassa.smartpossupply.dto.integration.quote;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteCreateRequestDto {

    @NotNull
    private Long agreementId;

    private String note;
}
