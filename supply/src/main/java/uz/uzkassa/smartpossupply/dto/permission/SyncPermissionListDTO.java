package uz.uzkassa.smartpossupply.dto.permission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PermissionType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SyncPermissionListDTO extends CommonDTO {

    String code;

    Integer position;

    boolean section;

    PermissionType permissionType;

    SyncPermissionListDTO parent;
}
