package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.permission.RolePermission;
import uz.uzkassa.smartpossupply.enums.PermissionType;
import uz.uzkassa.smartpossupply.enums.Role;

import java.util.List;

@Repository
public interface RolePermissionRepository extends BaseRepository<RolePermission, Long> {
    @Modifying
    void deleteAllByRoleAndPermission_PermissionType(Role role, PermissionType permissionType);

    @Query(value = "select p.code from RolePermission rp join rp.permission p where rp.role=:role")
    List<String> findAllPermissionCodesByRole(@Param("role") Role role);

    @Query(value = "select p.id from RolePermission rp join rp.permission p where rp.role=:role")
    List<Long> findAllPermissionIdsByRole(@Param("role") Role role);

    @Modifying
    void deleteAllByPermissionId(Long permissionId);
}
