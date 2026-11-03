package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.permission.UserPermission;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends BaseRepository<UserPermission, Long> {

    List<UserPermission> findAllByUserId(Long userId);

    Optional<UserPermission> findFirstByUserIdAndPermissionId(Long userId, Long permissionId);

    void deleteAllByUserId(Long userId);

    void deleteAllByPermissionId(Long permissionId);
}
