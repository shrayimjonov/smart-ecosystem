package uz.uzkassa.horeca.repository;

import org.springframework.cache.annotation.Cacheable;
import uz.uzkassa.horeca.constants.CacheConstants;
import uz.uzkassa.horeca.domain.unit.Unit;

public interface UnitRepository extends BaseRepository<Unit, String> {

    @Cacheable(cacheNames = CacheConstants.UNIT_BY_CODE_CACHE)
    Unit findFirstByCode(String code);
}
