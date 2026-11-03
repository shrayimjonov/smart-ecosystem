package uz.uzkassa.smartpos.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class VariationsDTO implements Serializable {
    @NotEmpty
    private Set<VariationValue> colours;
    private Set<String> prints;
    @NotBlank
    private String sizeKey;
    @NotEmpty
    private Set<AttributeValue> sizes;
}
