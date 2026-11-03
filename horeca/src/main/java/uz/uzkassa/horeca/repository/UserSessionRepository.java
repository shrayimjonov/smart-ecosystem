package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.auth.UserSession;
import uz.uzkassa.horeca.enums.Role;

import java.util.List;
import java.util.Optional;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 30.10.2024
 */

public interface UserSessionRepository extends BaseRepository<UserSession, Long> {

    Optional<UserSession> findFirstByFcmToken(String fcmToken);

    List<UserSession> findAllByUserIdOrderByLastUpdatedDateDesc(Long userId);

    List<UserSession> findAllByUser_DepartmentIdAndUser_RoleAndUser_DeletedIsFalse(Long departmentId, Role role);

    List<UserSession> findAllByUser_DepartmentIdAndUser_DeletedIsFalse(Long departmentId);

}
