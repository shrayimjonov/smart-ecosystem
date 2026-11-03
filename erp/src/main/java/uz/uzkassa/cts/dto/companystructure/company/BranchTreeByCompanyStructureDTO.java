package uz.uzkassa.cts.dto.companystructure.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchTreeByCompanyStructureDTO extends CommonDTO implements Serializable {

    List<BranchTreeByCompanyStructureDTO> children = new ArrayList<>();

    List<DepartmentTreeByCompanyStructureDTO> departments = new ArrayList<>();

    @JsonIgnore
    public void addChild(BranchTreeByCompanyStructureDTO child) {
        children.add(child);
    }

    @JsonIgnore
    public void addDepartment(DepartmentTreeByCompanyStructureDTO department) {
        departments.add(department);
    }
}

