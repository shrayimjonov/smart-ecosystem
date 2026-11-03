package uz.uzkassa.cts.dto.rentact.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RentActCreateRequestDto implements Serializable {
    @NotNull
    private Long customerId;
    @NotNull
    private Long offerId;
}
