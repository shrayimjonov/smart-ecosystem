package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.enums.Role;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends BaseRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findFirstByLoginAndDeletedIsFalse(String login);

    Optional<User> findByTinAndDeletedIsFalse(String tin);

    Optional<User> findByPinflAndDeletedIsFalse(String pinfl);

    boolean existsUserByLoginAndDeletedIsFalseAndIdNot(String login, Long id);

    boolean existsUserByLoginAndDeletedIsFalse(String login);

    boolean existsUserByTinAndDeletedIsFalseAndIdNot(String tin, Long id);

    boolean existsUserByPinflAndDeletedIsFalseAndIdNot(String pinfl, Long id);

    @Query("select count(u.id)>=1 from User u where u.deleted=false and u.companyId=:companyId and u.role =:role")
    boolean existsUserByCompanyIdAndRolesInAndDeletedIsFalse(@Param("companyId") Long companyId, @Param("role") Role role);

    @Query("select count(u.id)>=1 from User u where u.deleted=false and u.companyId=:companyId and u.role =:role and u.id !=:id")
    boolean existsUserByCompanyIdAndRolesInAndAndIdNotDeletedIsFalse(
        @Param("companyId") Long companyId,
        @Param("role") Role role,
        @Param("id") Long id
    );

    @Query("SELECT u.id from User u WHERE u.deleted=false and u.role=:role ")
    List<Long> getIdsByRoleAndDeletedIsFalse(@Param("role") Role role);
}
