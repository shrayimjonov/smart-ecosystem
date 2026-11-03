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
public class DepartmentTreeByCompanyStructureDTO extends CommonDTO implements Serializable {

    List<DepartmentTreeByCompanyStructureDTO> children = new ArrayList<>();

    List<EmployeeTreeByCompanyStructureDTO> employees = new ArrayList<>();

    @JsonIgnore
    public void addChild(DepartmentTreeByCompanyStructureDTO child) {
        children.add(child);
    }

    @JsonIgnore
    public void addEmployee(EmployeeTreeByCompanyStructureDTO employee) {
        employees.add(employee);
    }
}

