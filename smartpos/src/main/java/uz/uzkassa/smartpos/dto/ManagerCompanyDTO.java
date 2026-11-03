package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ManagerCompanyDTO {
    @NotNull
    private Long managerId;
    private List<Long> companyIds;
}
