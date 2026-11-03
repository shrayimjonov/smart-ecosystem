package uz.uzkassa.smartpos.dto.integration.quote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteChangeStatusRequestDto {
    @NotBlank
    private String id;
    @NotNull
    private String contractStatus;
}
