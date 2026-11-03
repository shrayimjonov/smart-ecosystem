package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.permission.Permission;
import uz.uzkassa.cts.dto.permission.PermissionDTO;
import uz.uzkassa.cts.dto.permission.PermissionTreeListDTO;
import uz.uzkassa.cts.dto.permission.admin.PermissionDetailAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface PermissionService extends BaseService<Permission, Long> {

    Long create(PermissionDTO permissionDTO);

    Long update(Long id, PermissionDTO permissionDTO);

    List<PermissionTreeListDTO> getTree(BaseFilter filter);

    PermissionDetailAdminDTO getForAdmin(Long id);

    void deleteById(Long id);
}
