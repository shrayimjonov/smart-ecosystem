package uz.uzkassa.cts.dto.group.admin;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupDetailAdminDTO extends CommonDTO implements Serializable {

    CommonDTO department;

    CommonDTO branch;

    CommonDTO company;

    CommonStatus status;
}
