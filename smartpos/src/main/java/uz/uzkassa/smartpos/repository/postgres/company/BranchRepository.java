package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uz.uzkassa.smartpos.constants.CacheConstants.ADMIN_COMPANY_MAIN_BRANCH_CACHE;

/**
 * Spring Data  repository for the Branch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch>, BranchRepositoryCustom {
    Optional<Branch> findByNameEqualsIgnoreCaseAndCompanyIdAndDeletedIsFalse(String name, Long companyId);

    /*
    @Query(value = "select distinct b.id from Branch b where b.deleted = false and b.groupId in (:groupIds)")
    Set<Long> findByGroupIdIn(@Param("groupIds") final Collection<Long> groupIds);
    */

    @Query(value = "select distinct b.id from Branch b join b.groups g where g.id in (:groups)")
    Set<Long> findIdsByGroups(Collection<Long> groups);

    List<Branch> findAllByCompanyIdAndFiscalIsTrueAndDeletedIsFalse(Long companyId);

    @Query(value = "select b from Branch b where b.deleted = false and b.companyId = :companyId ")
    List<Branch> findByCompanyId(@Param("companyId") Long companyId);

    Optional<Branch> findFirstByCtoId(Long ctoId);

    @Query(value = "select nextval('virtual_serial_number_seq')", nativeQuery = true)
    Integer nextVal();

    @Cacheable(cacheNames = ADMIN_COMPANY_MAIN_BRANCH_CACHE)
    Branch findFirstByCompanyIdAndMainBranchIsTrue(@Param("companyId") Long companyId);

    Optional<Branch> findFirstByOfdId(Long ofdId);

    Optional<Branch> findFirstByCompanyIdAndName(Long companyId, String name);

    List<Branch> findAllByIdInAndCompanyId(Collection<Long> ids, Long companyId);
}
