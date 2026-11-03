package uz.uzkassa.cts.dto.permission.admin;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.PermissionType;

import java.io.Serializable;

@Getter
@Setter
public class PermissionDetailAdminDTO extends CommonCodeDTO implements Serializable {

    Integer position;

    boolean section;

    CommonCodeDTO parent;

    PermissionType type;
}
