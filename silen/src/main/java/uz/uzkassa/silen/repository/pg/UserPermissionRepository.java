package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.domain.UserPermission;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    List<UserPermission> findAllByUserId(String userId);

    @Query("select o.permission.code from UserPermission o where o.userId=:userId")
    List<String> findAllPermissionCodeByUserId(@Param("userId") String userId);

    @Modifying
    void deleteAllByUserId(String userId);

    Optional<UserPermission> findFirstByUserIdAndPermissionId(String userId, Long permissionId);
}
