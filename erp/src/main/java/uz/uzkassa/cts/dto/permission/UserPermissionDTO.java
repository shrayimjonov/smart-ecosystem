package uz.uzkassa.cts.dto.permission;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserPermissionDTO implements Serializable {

    Long userId;

    List<String> permissionCodes;
}
