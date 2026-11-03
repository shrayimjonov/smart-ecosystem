package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.permission.RolePermission;
import uz.uzkassa.cts.enums.Role;

import java.util.List;

@Repository
public interface RolePermissionRepository extends BaseRepository<RolePermission, Long> {

    List<RolePermission> findAllByRole(Role role);

    @Query(value = "select rp.permissionId from RolePermission rp WHERE rp.role=:role")
    List<Long> findAllPermissionIdsByRole(@Param("role") Role role);

    void deleteAllByPermissionId(Long permissionId);
}
