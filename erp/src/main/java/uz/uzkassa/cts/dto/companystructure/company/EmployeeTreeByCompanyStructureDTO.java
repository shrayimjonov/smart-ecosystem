package uz.uzkassa.cts.dto.companystructure.company;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeTreeByCompanyStructureDTO extends FullNameDTO implements Serializable {

    CommonDTO position;

}
