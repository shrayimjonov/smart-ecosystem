package uz.uzkassa.smartpos.repository.postgres.user;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.constants.CacheConstants;
import uz.uzkassa.smartpos.domain.postgres.user.User;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, UserRepositoryCustom {

    Optional<User> findOneByLoginAndActivationKey(String login, String activationKey);

    Optional<User> findOneByLoginAndResetKey(String login, String key);

    @Cacheable(cacheNames = CacheConstants.USERS_BY_LOGIN_CACHE)
    Optional<User> findOneByLogin(String login);

    Optional<User> findFirstByRefreshToken(String refreshToken);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = CacheConstants.USERS_WITH_AUTHORITY_CACHE)
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    User findFirstByBranchIdAndDeletedIsFalse(Long branchId);

}
