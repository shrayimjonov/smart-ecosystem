package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.settings.GenericSettings;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;

import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@Repository
public interface GenericSettingsRepository extends BaseRepository<GenericSettings, Long> {

    Optional<GenericSettings> findFirstBySettingsType(GenericSettingsType settingsType);

    @Cacheable(cacheNames = GENERIC_SETTINGS_BY_TYPE_CACHE)
    Optional<GenericSettings> findBySettingsType(GenericSettingsType settingsType);

    List<GenericSettings> findAllByForCabinetOrderByIdAsc(boolean cabinet);

    List<GenericSettings> findAllByForCabinetOrderByOrderNumberAscIdAsc(boolean cabinet);


    List<GenericSettings> findAllByOrderByOrderNumberAscIdAsc();
}
