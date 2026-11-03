package uz.uzkassa.cts.dto.companystructure.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyStructureCompanyDTO extends CompanyCommonDTO implements Serializable {

    List<BranchTreeByCompanyStructureDTO> branches = new ArrayList<>();

    @JsonIgnore
    public void addBranch(BranchTreeByCompanyStructureDTO branch) {
        branches.add(branch);
    }
}

