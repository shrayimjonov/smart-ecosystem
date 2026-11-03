package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.address.District;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends BaseRepository<District, Long> {
    @Cacheable(cacheNames = DISTRICT_BY_REGION_CODE_CACHE)
    Optional<District> findByRegionCodeAndCode(Long regionCode, Long districtCode);

    @Cacheable(cacheNames = DISTRICT_LIST_BY_REGION_ID_CACHE)
    List<District> findAllByRegion_Id(Long regionId);
}
