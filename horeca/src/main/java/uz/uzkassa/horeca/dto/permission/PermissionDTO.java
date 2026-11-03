package uz.uzkassa.horeca.dto.permission;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.PermissionType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PermissionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 345435L;

    String name;

    String code;

    Integer position;

    boolean section;

    Long parentId;

    PermissionType permissionType;
}
