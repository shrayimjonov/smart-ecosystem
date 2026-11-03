package uz.uzkassa.cts.dto.rentact.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentActUpdateRequestDto extends RentActRequestDto {
    @NotNull
    private Long id;
}
