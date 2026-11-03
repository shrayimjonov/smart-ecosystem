package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.address.Region;

import java.util.List;

public interface RegionRepository extends BaseRepository<Region, Long> {

    @Cacheable(cacheNames = CacheConstants.REGION_ENTITY_BY_CODE_CACHE)
    Region findByCode(Long code);

    @Cacheable(cacheNames = CacheConstants.REGION_ENTITY_LIST_CACHE)
    List<Region> findAllByDeletedIsFalse();
}
