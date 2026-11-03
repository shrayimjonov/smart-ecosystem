package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.ActivityType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityTypeRepository extends BaseRepository<ActivityType, Long> {

    Optional<ActivityType> findByCodeAndParent_Code(Long code, Long parentCode);

    @Cacheable(cacheNames = CacheConstants.ACTIVITY_ENTITY_PARENT_LIST_CACHE)
    List<ActivityType> findAllByParentIdNull();

    @Cacheable(cacheNames = CacheConstants.ACTIVITY_ENTITY_LIST_BY_PARENT_ID_CACHE)
    List<ActivityType> findAllByParentId(Long parentId);
}
