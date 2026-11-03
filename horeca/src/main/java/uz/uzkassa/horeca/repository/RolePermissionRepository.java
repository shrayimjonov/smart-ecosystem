package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.permission.RolePermission;
import uz.uzkassa.horeca.enums.Role;

import java.util.List;


public interface RolePermissionRepository extends BaseRepository<RolePermission, Long> {
    @Modifying
    void deleteAllByPermissionId(Long permissionId);

    boolean existsByRole(Role role);

    @Modifying
    void deleteAllByRole(Role role);

    @Query(value = "SELECT rp.permission.code FROM RolePermission rp WHERE rp.role=:role")
    List<String> findAllPermissionCodesByRole(@Param("role") Role role);
}
