package uz.uzkassa.smartpossupply.dto.permission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PermissionType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionDTO  extends CommonDTO implements Serializable {

    String code;

    Integer position;

    boolean section;

    Long parentId;

    PermissionType permissionType;
}
