package uz.uzkassa.smartpos.repository.postgres.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.user.UserPermission;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    Optional<UserPermission> findByUserIdAndPermissionCode(Long userId, String permissionCode);

    @Query(value = "select p.permissionCode from UserPermission p where p.hasAccess=true and p.userId=?1")
    List<String> findAllPermissionCodes(Long userId);

    @Modifying
    @Query(value = "update UserPermission set hasAccess=?1 where userId=?2")
    void updateUserPermission(boolean hasAccess, Long userId);

    @Modifying
    void deleteAllByPermissionCode(String permissionCode);

}
