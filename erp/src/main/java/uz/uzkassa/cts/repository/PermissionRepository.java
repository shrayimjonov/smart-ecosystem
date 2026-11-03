package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.permission.Permission;
import uz.uzkassa.cts.enums.PermissionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {

    Optional<Permission> findFirstByCodeAndDeletedIsFalse(String code);

    boolean existsByCodeAndDeletedIsFalse(String code);

    boolean existsByCodeAndDeletedIsFalseAndIdNot(String code, Long id);

    List<Permission> findByParentIdAndTypeAndDeletedIsFalseOrderByPositionAsc(Long parentId, PermissionType type);

}
