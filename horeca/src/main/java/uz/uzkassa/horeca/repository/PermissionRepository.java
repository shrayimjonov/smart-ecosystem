package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.permission.Permission;
import uz.uzkassa.horeca.enums.PermissionType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends BaseRepository<Permission, Long> {
    Optional<Permission> findByCodeAndDeletedFalse(String code);

    Optional<Permission> findByCodeAndIdNotAndDeletedIsFalse(String code, Long id);

    Set<Permission> findByParentIdAndPermissionTypeAndDeletedIsFalseOrderByPositionAsc(Long parentId, PermissionType permissionType);

    @Query("SELECT p FROM Permission p WHERE p.permissionType=:permissionType ORDER BY p.parentId desc ")
    List<Permission> getAll(@Param("permissionType") PermissionType permissionType);
}
