package uz.uzkassa.cts.dto.permission;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.enums.PermissionType;

import java.io.Serializable;

@Getter
@Setter
public class PermissionDTO extends LocalizedDTO implements Serializable {

    String code;

    Integer position;

    boolean section;

    Long parentId;

    PermissionType type;
}
