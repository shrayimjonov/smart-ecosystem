package uz.uzkassa.cts.dto.rentoffer.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RentOfferRequestDto implements Serializable {
    @NotNull
    private Long customerId;
}
