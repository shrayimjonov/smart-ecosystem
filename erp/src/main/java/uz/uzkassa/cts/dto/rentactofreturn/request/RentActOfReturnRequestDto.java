package uz.uzkassa.cts.dto.rentactofreturn.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RentActOfReturnRequestDto {
    @NotNull
    private Long customerId;
    @NotEmpty
    public List<Long> accessoryIds;
}
