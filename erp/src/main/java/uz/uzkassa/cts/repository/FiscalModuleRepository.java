package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.repository.custom.FiscalModuleRepositoryCustom;

@Repository
public interface FiscalModuleRepository extends BaseRepository<FiscalModule, Long>, FiscalModuleRepositoryCustom {

    @Cacheable(cacheNames = CacheConstants.FISCAL_MODULE_BY_SERIAL_NUMBER)
    FiscalModule findBySerialNumber(String serialNumber);

    FiscalModule findFirstBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}
