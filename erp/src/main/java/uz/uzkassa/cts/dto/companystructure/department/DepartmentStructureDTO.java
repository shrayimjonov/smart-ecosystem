package uz.uzkassa.cts.dto.companystructure.department;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.companystructure.position.PositionStructureEmployeeDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentStructureDTO extends CommonDTO implements Serializable {

    List<DepartmentStructureEmployeeDTO> employees = new ArrayList<>();

    List<DepartmentStructureDTO> children = new ArrayList<>();

    @JsonIgnore
    public void addChild(DepartmentStructureDTO child) {
        children.add(child);
    }

    @JsonIgnore
    public void addEmployee(DepartmentStructureEmployeeDTO employee) {
        employees.add(employee);
    }
}

