package uz.uzkassa.cts.dto.companystructure.supervisor;

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
public class SupervisorStructureDTO extends CommonDTO implements Serializable {

    List<SupervisorStructureEmployeeDTO> employees = new ArrayList<>();

    List<SupervisorStructureDTO> children = new ArrayList<>();

    @JsonIgnore
    public void addChild(SupervisorStructureDTO child) {
        children.add(child);
    }

    @JsonIgnore
    public void addEmployee(SupervisorStructureEmployeeDTO employee) {
        employees.add(employee);
    }
}

