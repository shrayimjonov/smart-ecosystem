package uz.uzkassa.cts.dto.rentactofreturn.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RentActOfReturnUpdateRequestDto implements Serializable {
    @NotNull
    private Long id;

    @NotEmpty
    public List<Long> accessoryIds;
}
