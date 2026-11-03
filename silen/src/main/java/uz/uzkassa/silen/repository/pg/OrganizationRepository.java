package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Organization;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data  repository for the Factory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String>, OrganizationRepositoryCustom {

    Optional<Organization> findFistByTinAndDeletedIsFalse(String tin);

    @Query("from Organization where (tin=:tinOrId or id=:tinOrId) and deleted is not true")
    Optional<Organization> findFirstByDeletedIsFalseAndTinOrId(@Param("tinOrId") String tinOrId);

    Optional<Organization> findFirstByIdAndDeletedIsFalseAndActiveIsTrue(String id);

    List<Organization> findAllByTinInAndDeletedIsFalse(Set<String> tin);

    boolean existsOrganizationByTinInAndDeletedIsFalse(Set<String> asSet);

    List<Organization> findAllByTrueTokenIsNotNullAndTokenDateBetween(LocalDateTime tokenDate, LocalDateTime tokenDate2);

    @Query("select tin from Organization where managerId in (:managerIds) and deleted is not true")
    List<String> findTinsByManagerIds(@Param("managerIds") List<String> managerIds);

    List<Organization> findAllByDemoAllowedIsTrueAndDeletedIsFalseAndActiveIsTrue();

    List<Organization> findAllByParentIdAndDeletedIsFalseAndActiveIsTrue(String parentId);
    List<Organization> findAllByDeletedIsFalseAndActiveIsTrue();

    List<Organization> findAllByTrueTokenIsNotNull();
}
