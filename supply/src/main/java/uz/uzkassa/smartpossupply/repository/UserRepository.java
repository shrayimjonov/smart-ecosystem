package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.constants.CacheConstants;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends BaseRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findFirstByRefreshToken(String refreshToken);

    boolean existsUserByLoginAndIdNot(String login, Long id);

    boolean existsUserByLoginAndDeletedIsFalse(String login);

    boolean existsUserByPhoneAndDeletedIsFalse(String phone);

    @Query("select distinct u from User u join u.branches branches where u.deleted = false and branches.id =:branchId and u.status=:status and u.role in(:roles)")
    List<User> findAllByBranchIdAndStatusAndRoleInAndDeletedIsFalse(@Param("branchId") Long branchId, @Param("status") UserStatus status, @Param("roles") List<Role> roles);

    List<User> findAllByCompanyIdAndStatusAndRoleInAndDeletedIsFalse(Long companyId, UserStatus status, List<Role> roles);

    @Cacheable(cacheNames = CacheConstants.USER_BY_LOGIN_CACHE)
    Optional<User> findFirstByLoginAndDeletedIsFalse(String login);

    @Query("SELECT u.companyId FROM User u WHERE u.id = :id")
    Long getCompanyIdByUserId(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.login = :login AND u.deleted = false")
    Optional<User> findFirstByLogin(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deleted = false")
    @EntityGraph(attributePaths = {"warehouse", "supervisor", "branches"})
    Optional<User> getDetailForCabinet(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.login = :login AND u.deleted = false")
    @EntityGraph(attributePaths = {"company", "branch", "warehouse", "company.permissions"})
    Optional<User> findFirstByLoginForCabinet(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.login = :login AND u.deleted = false")
    Optional<User> findFirstByLoginForAdmin(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.login = :login AND u.deleted = false")
    @EntityGraph(attributePaths = {"company", "company.activityType", "branch", "warehouse", "photo"})
    Optional<User> findFirstByLoginForMobile(@Param("login") String login);

    @Query("select u.id from User u where u.deleted=false and u.role =:role")
    List<Long> getIdsByRole(@Param("role") Role role);

    @Query("select distinct u from User u where u.deleted=false and u.companyId=:companyId and u.role=:role order by u.id desc limit 1")
    Optional<User> findFirstByCompanyIdAndRole(@Param("companyId") Long companyId, @Param("role") Role role);

    @Modifying
    @Query("update User set supervisorId=:supervisorId where id in (:managerIds)")
    void assignSupervisor(@Param("supervisorId") Long supervisorId, @Param("managerIds") List<Long> managerIds);

    @Query("select u.id from User u where u.deleted=false and u.supervisorId=:supervisorId")
    List<Long> getIdsBySupervisorId(@Param("supervisorId") Long supervisorId);

    Set<User> findAllByDeletedIsFalseAndRoleAndIdIn(Role role, Set<Long> employeeIds);

    @Query("""
                SELECT COUNT(u)
                FROM User u
                WHERE u.deleted = false
                  AND u.id IN :employeeIds
                  AND u.role IN :roles
                  AND u.branchId = :branchId
            """)
    long countValidEmployees(@Param("roles") Collection<Role> roles,
                             @Param("branchId") Long branchId,
                             @Param("employeeIds") Set<Long> employeeIds);

    @Query("""
                select u.id from User u
                where u.companyId = :companyId
                and u.telegramUserId = :telegramUserId
            """)
    Long findIdByCompanyIdAndTelegramUserId(@Param("companyId") Long companyId, @Param("telegramUserId") Long telegramUserId);

    Optional<User> findFirstByCompanyIdAndPhone(Long companyId, String phone);

    @EntityGraph(attributePaths = {"branches"})
    Optional<User> findFirstByIdAndRole(Long id, Role role);

    List<User> findAllByCompanyIdAndDeletedIsFalse(Long companyId);

    Optional<User> findByIdAndDeletedIsFalse(Long id);
}
