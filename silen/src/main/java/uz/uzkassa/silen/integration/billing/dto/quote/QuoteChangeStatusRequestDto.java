package uz.uzkassa.silen.integration.billing.dto.quote;


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
    @NotNull
    private String id;
    @NotNull
    private String contractStatus;
}
