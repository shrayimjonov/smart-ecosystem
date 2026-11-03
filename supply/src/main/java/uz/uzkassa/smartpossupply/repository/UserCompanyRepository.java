package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.superuser.UserCompany;

import java.util.List;

public interface UserCompanyRepository extends BaseRepository<UserCompany, Long> {
    @EntityGraph(attributePaths = {"user", "company"})
    List<UserCompany> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "company"})
    Page<UserCompany> findAllByUserId(Long userId, Pageable pageable);

    @Query("select uc.user from UserCompany uc where uc.deleted = false")
    Page<User> findAllUsers(Pageable pageable);
}
