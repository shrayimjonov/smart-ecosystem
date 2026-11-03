package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.silen.domain.RolePermission;
import uz.uzkassa.silen.enumeration.UserRole;

import java.util.List;
import java.util.Set;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Query(value = "select p.code from RolePermission rp join rp.permission p where rp.role=:role")
    Set<String> findAllPermissionCodesByRole(@Param("role") UserRole role);

    @Query(value = "select p.id from RolePermission rp join rp.permission p where rp.role=:role")
    List<Long> findAllPermissionIdsByRole(@Param("role") UserRole role);

    void deleteAllByRole(UserRole role);

    @Modifying
    void deleteAllByPermissionCode(String permissionCode);
}
