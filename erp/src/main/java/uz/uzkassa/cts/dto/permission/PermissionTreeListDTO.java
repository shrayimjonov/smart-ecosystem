package uz.uzkassa.cts.dto.permission;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.PermissionType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class PermissionTreeListDTO extends CommonCodeDTO implements Serializable {

    Integer position;

    boolean section;

    CommonCodeDTO parent;

    PermissionType type;

    List<PermissionTreeListDTO> children;
}
