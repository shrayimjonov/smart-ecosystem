package uz.uzkassa.smartpossupply.dto.company;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentCompanyDTO {
    @NotNull
    private Long parentCompanyId;

    @NotNull
    private Long childCompanyId;
}