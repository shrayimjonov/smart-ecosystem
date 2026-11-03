package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Unit;

import java.util.List;

@Repository
public interface UnitRepository extends BaseRepository<Unit, Long> {
    @Cacheable(cacheNames = UNIT_LIST_CACHE)
    List<Unit> findAllByDeletedIsFalseOrderBySorder();

    @Cacheable(cacheNames = UNIT_BY_CODE_CACHE)
    Unit findFirstByCode(String code);
}
