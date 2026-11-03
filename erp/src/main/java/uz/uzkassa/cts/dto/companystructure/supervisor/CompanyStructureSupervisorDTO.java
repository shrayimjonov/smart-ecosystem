package uz.uzkassa.cts.dto.companystructure.supervisor;

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
public class CompanyStructureSupervisorDTO extends CompanyCommonDTO implements Serializable {

    List<SupervisorStructureDTO> supervisors = new ArrayList<>();

    public void addSupervisor(SupervisorStructureDTO supervisor) {
        this.supervisors.add(supervisor);
    }
}

