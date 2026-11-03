package uz.uzkassa.smartpos.dto.register;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RegisterFillMarkingsDTO {
    @NotNull
    private Long productId;
    private Long branchId;
    @NotEmpty
    private Set<String> markings;
    private Set<String> aggregations;
}
