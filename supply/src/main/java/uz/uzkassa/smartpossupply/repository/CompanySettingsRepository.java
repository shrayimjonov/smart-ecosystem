package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@Repository
public interface CompanySettingsRepository extends BaseRepository<CompanySettings, Long> {

    Optional<CompanySettings> findFirstByCompanyIdAndSettingsId(Long companyId, Long settingsId);

    Optional<CompanySettings> findFirstByCompanyIdAndSettings_SettingsType(Long companyId, GenericSettingsType settingsType);

    @Cacheable(cacheNames = COMPANY_SETTINGS_BY_TYPE_CACHE)
    boolean existsByCompanyIdAndSettings_SettingsType(Long companyId, GenericSettingsType settingsType);

}
