package uz.uzkassa.horeca.dto.permission;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.PermissionType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRolePermissionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5849584L;

    List<String> permissions;

    PermissionType permissionType;
}
