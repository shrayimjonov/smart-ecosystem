package uz.uzkassa.smartpos.dto.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ColorGroupUpdateDTO implements Serializable {
    @NotNull
    private Long productId;
    @NotNull
    private Long branchId;
    private String printName;
    private BigDecimal price;
    private List<ColorGroupImageDTO> images;
    @NotEmpty
    private Set<AttributeValue> sizes;
}
