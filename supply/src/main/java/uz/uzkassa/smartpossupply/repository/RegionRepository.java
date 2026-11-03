package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.address.Region;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends BaseRepository<Region, Long> {
    @Cacheable(cacheNames = REGION_BY_CODE_CACHE)
    Optional<Region> findByCode(Long code);

    @Cacheable(cacheNames = REGION_LIST_CACHE)
    List<Region> findAll();
}
