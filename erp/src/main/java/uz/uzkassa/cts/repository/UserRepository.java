package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.permission.Authority;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.repository.custom.UserRepositoryCustom;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends BaseRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findOneByLoginAndDeletedIsFalse(String login);

    Optional<User> findFirstByCompanyIdAndStatusAndAuthoritiesInAndDeletedIsFalse(Long companyId, UserStatus status, Set<Authority> authorities);

    User findByLoginAndDeletedIsFalse(String login);

    List<User> findAllByStatusAndBirthdayAndAuthoritiesInAndDeletedIsFalse(UserStatus status, LocalDate birthday, Set<Authority> authorities);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = CacheConstants.USER_BY_LOGIN_CACHE)
    Optional<User> findOneWithAuthoritiesByLoginAndDeletedIsFalse(String login);

    boolean existsByLoginAndDeletedIsFalse(String login);

    boolean existsByLoginAndIdNotAndDeletedIsFalse(String login, Long id);

    boolean existsByPhoneAndDeletedIsFalse(String phone);

    boolean existsByPhoneAndIdNotAndDeletedIsFalse(String phone, Long id);
}
