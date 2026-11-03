package uz.uzkassa.smartpossupply.dto.permission;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PermissionType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRolePermissionDTO implements Serializable {

    List<String> permissions;

    PermissionType permissionType;

    boolean applyAllUsers = false;
}
