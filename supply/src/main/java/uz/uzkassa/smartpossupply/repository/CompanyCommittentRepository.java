package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.05.2023 16:46
 */
@Repository
public interface CompanyCommittentRepository extends BaseRepository<CompanyCommittent, Long>, CompanyCommittentRepositoryCustom {

    Optional<CompanyCommittent> findFirstByCompanyIdAndCommittentIdOrderByTokenTimeDesc(Long companyId, Long committentId);

    List<CompanyCommittent> findAllByCompanyIdAndCommittentIdOrderByTokenTimeDesc(Long companyId, Long committentId);

    List<CompanyCommittent> findAllByCompanyIdInAndCommittentIdOrderByTokenTimeDesc(Collection<Long> companyIds, Long committentId);
}
