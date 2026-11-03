package uz.uzkassa.horeca.repository;

import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.company.CompanySettings;

import java.util.Optional;

public interface CompanySettingsRepository extends BaseRepository<CompanySettings, Long> {
    Optional<CompanySettings> findFirstByCompanyId(@Param("companyId") Long companyId);
}
