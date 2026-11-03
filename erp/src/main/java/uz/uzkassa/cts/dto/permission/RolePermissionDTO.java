package uz.uzkassa.cts.dto.permission;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.Role;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RolePermissionDTO implements Serializable {

    Role role;

    List<String> permissionCodes;
}
