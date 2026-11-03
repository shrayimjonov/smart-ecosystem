package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.Permission;
import uz.uzkassa.silen.enumeration.PermissionType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Set<Permission> findByParentIdAndPermissionTypeAndDeletedIsFalseOrderByPositionAsc(Long parentId, PermissionType type);

    List<Permission> findAllByCodeIn(List<String> codes);

    Optional<Permission> findByCodeAndDeletedFalse(String code);

    Optional<Permission> findByCodeAndIdNotAndDeletedIsFalse(String code, Long id);
}
