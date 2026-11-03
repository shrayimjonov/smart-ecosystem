package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.permission.RolePermission;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.permission.RolePermissionDTO;
import uz.uzkassa.cts.enums.Role;

import java.util.List;

public interface RolePermissionService extends BaseService<RolePermission, Long> {
    List<String> create(RolePermissionDTO rolePermissionDTO);

    List<CommonCodeDTO> getListByRole(Role role);
}
