package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.company.ActivityType;
import uz.uzkassa.smartpos.domain.postgres.company.Company;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uz.uzkassa.smartpos.constants.CacheConstants.ADMIN_COMPANY_CACHE;

/**
 * Spring Data  repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company>, CompanyRepositoryCustom {
    List<Company> findByCtoIdOrInnIn(Long ctoId, Collection<String> inns);

    @Query("select c from Company c where c.trialDate <= ?1")
    List<Company> findAllByTrialDateLessThanEqual(LocalDateTime trialDate);

    Optional<Company> findOneByName(String name);

    Optional<Company> findOneByInn(String inn);

    boolean existsCompanyByInn(String inn);

    boolean existsCompanyByInnIn(Set<String> inns);

    Optional<Company> findFirstByCtoId(Long ctoId);

    List<Company> findAllByCtoIdIsNotNull();

    @Modifying
    @Query("update Company set ownerId=null where ownerId=?1")
    void removeUserFromOwner(Long userId);

    @Query(value = "SELECT c.activityTypes FROM Company c WHERE c.id=?1")
    Set<ActivityType> getActivityTypesByCompanyId(Long id);

    List<Company> findAllByInnIn(List<String> strings);

    List<Company> findAllByIdIn(List<Long> ids);

    Optional<Company> findByInnOrOldInn(String inn, String oldInn);

    Optional<Company> findOneByApayId(String apayId);

    @Cacheable(cacheNames = ADMIN_COMPANY_CACHE)
    Optional<Company> findOneByAdminIsTrue();

    @Modifying
    @Query("update Company set managerId=null where managerId=?1")
    void clearManager(Long managerId);
}
