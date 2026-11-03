package uz.uzkassa.cts.dto.companystructure.position;

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
public class PositionStructureDTO extends CommonDTO implements Serializable {

    List<PositionStructureEmployeeDTO> employees = new ArrayList<>();

    List<PositionStructureDTO> children = new ArrayList<>();

    @JsonIgnore
    public void addChild(PositionStructureDTO child) {
        children.add(child);
    }

    @JsonIgnore
    public void addEmployee(PositionStructureEmployeeDTO employee) {
        employees.add(employee);
    }
}

