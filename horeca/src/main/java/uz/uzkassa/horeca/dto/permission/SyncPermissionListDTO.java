package uz.uzkassa.horeca.dto.permission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.PermissionType;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SyncPermissionListDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 348593894L;

    String code;

    Integer position;

    boolean section;

    PermissionType permissionType;

    SyncPermissionListDTO parent;
}
