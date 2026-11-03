package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ProductGroup;

import java.util.Set;

@Getter
@Setter
public class MarkingCheckDto {
    @NotNull
    private Long companyId;
    @NotEmpty
    private Set<String> codes;
    private String mxik;
    private ProductGroup productGroup;
}
