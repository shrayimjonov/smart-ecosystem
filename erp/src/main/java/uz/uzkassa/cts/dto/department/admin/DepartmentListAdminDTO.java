package uz.uzkassa.cts.dto.department.admin;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentListAdminDTO extends LocalizedDTO implements Serializable {

    CommonStatus status;

    CommonDTO parent;

    CommonDTO branch;

    CommonDTO company;
}
