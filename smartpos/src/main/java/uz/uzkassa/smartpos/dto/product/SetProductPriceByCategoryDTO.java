package uz.uzkassa.smartpos.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SetProductPriceByCategoryDTO implements Serializable {
    @NotNull
    private Long categoryId;
    @NotNull
    private Long branchId;
    @NotNull
    private Double coefficient;
    private boolean decrease;
}
