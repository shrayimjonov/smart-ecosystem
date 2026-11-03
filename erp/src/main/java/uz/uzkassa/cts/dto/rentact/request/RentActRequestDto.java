package uz.uzkassa.cts.dto.rentact.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RentActRequestDto {
    @NotNull
    private Long customerId;
    @NotNull
    private Long actId;
    @NotEmpty
    public Set<RentActAccessoryRequestDto> accessories;

}
