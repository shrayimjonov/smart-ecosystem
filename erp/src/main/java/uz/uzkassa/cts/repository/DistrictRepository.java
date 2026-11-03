package uz.uzkassa.cts.repository;


import org.springframework.cache.annotation.Cacheable;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.address.District;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends BaseRepository<District, Long> {

    List<District> findAllByDeletedIsFalseOrderById();

    @Cacheable(cacheNames = CacheConstants.DISTRICT_ENTITY_BY_CODE_AND_REGION_ID_CACHE)
    Optional<District> findFirstByRegionIdAndCode(Long regionId, Long code);

    @Cacheable(cacheNames = CacheConstants.DISTRICT_ENTITY_LIST_BY_REGION_ID_CACHE)
    List<District> findAllByRegionId(Long regionId);
}
