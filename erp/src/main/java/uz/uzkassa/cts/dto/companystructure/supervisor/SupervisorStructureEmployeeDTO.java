package uz.uzkassa.cts.dto.companystructure.supervisor;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.file.FileDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupervisorStructureEmployeeDTO extends FullNameDTO implements Serializable {

    FileDTO photo;

    CommonDTO position;

    CommonDTO department;
}
