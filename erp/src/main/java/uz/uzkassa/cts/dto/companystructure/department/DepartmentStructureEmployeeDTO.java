package uz.uzkassa.cts.dto.companystructure.department;


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
public class DepartmentStructureEmployeeDTO extends FullNameDTO implements Serializable {

    FileDTO photo;

    CommonDTO position;
}
