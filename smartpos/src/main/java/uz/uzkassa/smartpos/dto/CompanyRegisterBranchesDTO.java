package uz.uzkassa.smartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRegisterBranchesDTO implements Serializable {
    private Long companyId;
    private List<Long> branchIds;
}
