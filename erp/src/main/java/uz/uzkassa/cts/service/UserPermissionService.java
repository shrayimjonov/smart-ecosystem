package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.permission.UserPermission;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.cts.dto.permission.UserPermissionDTO;

import java.util.List;

public interface UserPermissionService extends BaseService<UserPermission, Long> {
    void create(UserPermissionDTO userPermissionDTO);

    void create(UserRolePermissionPayload userRolePermissionPayload);

    List<CommonCodeDTO> getListByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
