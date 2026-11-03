package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.permission.Permission;
import uz.uzkassa.smartpossupply.enums.PermissionType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
    Set<Permission> findByParentIdAndPermissionTypeAndDeletedIsFalseOrderByPositionAsc(Long parentId, PermissionType permissionType);

    @Query("select p from Permission p where p.permissionType=:permissionType order by p.parentId desc")
    List<Permission> getAll(@Param("permissionType") PermissionType permissionType);

    List<Permission> findAllByPermissionTypeAndCodeIn(PermissionType permissionType, List<String> codes);

    Optional<Permission> findByCodeAndPermissionTypeAndDeletedFalse(String code, PermissionType permissionType);

    Optional<Permission> findByCodeAndPermissionTypeAndIdNotAndDeletedIsFalse(String code, PermissionType permissionType, Long id);
}
