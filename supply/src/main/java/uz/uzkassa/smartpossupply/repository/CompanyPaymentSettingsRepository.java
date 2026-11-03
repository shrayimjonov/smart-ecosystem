package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyPaymentSettings;

import java.util.Optional;

@Repository
public interface CompanyPaymentSettingsRepository extends BaseRepository<CompanyPaymentSettings, Long> {

    Optional<CompanyPaymentSettings> findFirstByCompanyId(Long companyId);
}
