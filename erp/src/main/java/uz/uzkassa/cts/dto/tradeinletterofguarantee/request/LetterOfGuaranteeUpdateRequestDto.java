package uz.uzkassa.cts.dto.tradeinletterofguarantee.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetterOfGuaranteeUpdateRequestDto extends LetterOfGuaranteeRequestDto{
    @NotNull
    private Long id;
}
