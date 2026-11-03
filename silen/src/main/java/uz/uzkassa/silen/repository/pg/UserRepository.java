package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.silen.config.CacheConstants;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.enumeration.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
//@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom, CacheConstants {
    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByLoginAndDeletedFalse(String login);

    List<User> findAllByOrganizationId(String organizationId);

    Long countAllByOrganizationIdAndDeletedFalseAndActivatedAndRoleEquals(String organizationId,boolean isActivated, UserRole userRole);

    @Query("select id from User where supervisorId=:supervisorId")
    List<String> getSupervisorManagers(@Param("supervisorId") String supervisorId);

    List<User> findAllByRole(UserRole role);

    @Query("select u.id from User u where  u.role=:role")
    List<String> findAllIdByRole(@Param("role") UserRole userRole);
}
