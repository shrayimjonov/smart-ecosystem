package uz.uzkassa.smartpos.repository.postgres.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyExtraInfo;

import java.util.Optional;

@Repository
public interface CompanyExtraInfoRepository extends JpaRepository<CompanyExtraInfo, Long> {
    Optional<CompanyExtraInfo> findByCompanyId(Long companyId);
}
