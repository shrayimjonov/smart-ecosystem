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
public class GroupListAdminDTO extends CommonDTO implements Serializable {

    CommonStatus status;

    CommonDTO department;

    CommonDTO branch;

    CommonDTO company;
}
