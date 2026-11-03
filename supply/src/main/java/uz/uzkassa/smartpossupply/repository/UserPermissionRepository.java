package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.permission.UserPermission;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends BaseRepository<UserPermission, Long> {
    @Query(value = "select p.code from UserPermission up join up.permission p where up.userId=:userId and up.hasAccess=true ")
    List<String> findAllPermissionCodesByUserId(@Param("userId") Long userId);

    @Modifying
    void deleteAllByPermissionId(Long permissionId);

    @Modifying
    void deleteAllByUserId(Long userId);

    Optional<UserPermission> findFirstByUserIdAndPermissionId(Long userId, Long permissionId);

    @Modifying
    @Query("update UserPermission up set up.hasAccess=false, up.manual=false where up.userId=:userId and up.permissionId not in(:permissionIds)")
    void updateAccess(
        @Param("userId") Long userId,
        @Param("permissionIds") List<Long> permissionIds
    );

    @Modifying
    @Query("update UserPermission up set up.hasAccess=false where up.userId=:userId and up.permissionId not in(:permissionIds) and up.manual=false")
    void updateAccessByRole(
        @Param("userId") Long userId,
        @Param("permissionIds") List<Long> permissionIds
    );

    @Modifying
    @Query("update UserPermission up set up.hasAccess=false where up.userId=:userId and up.manual=false")
    void updateAllAccess(
        @Param("userId") Long userId
    );
}
