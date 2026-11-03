package uz.uzkassa.cts.dto.companystructure.department;

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
public class CompanyStructureDepartmentDTO extends CompanyCommonDTO implements Serializable {

    List<DepartmentStructureDTO> departments = new ArrayList<>();

    public void addDepartment(DepartmentStructureDTO department) {
        this.departments.add(department);
    }
}

