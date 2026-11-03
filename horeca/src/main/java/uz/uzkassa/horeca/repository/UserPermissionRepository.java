package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.permission.UserPermission;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository extends BaseRepository<UserPermission, Long> {
    @Modifying
    void deleteAllByUserId(Long userId);

    @Modifying
    void deleteAllByPermissionId(Long permissionId);

    Optional<UserPermission> findFirstByUserIdAndPermissionId(Long userId, Long permissionId);

    @Modifying
    @Query("update UserPermission set hasAccess=:hasAccess where userId=:userId and permissionId not in(:permissionIds)")
    void updateAccess(
        @Param("userId") Long userId,
        @Param("permissionIds") List<Long> permissionIds,
        @Param("hasAccess") boolean hasAccess
    );

    @Query(value = "SELECT p.permission.code from UserPermission  p WHERE p.userId=:userId and p.hasAccess=true")
    List<String> findAllPermissionCodesByUserId(@Param("userId") Long userId);

    Optional<UserPermission> findByUserIdAndPermissionId(Long userId, Long permissionId);
}
