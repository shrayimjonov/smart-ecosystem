package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.ActivityType;

import java.util.List;

@Repository
public interface ActivityTypeRepository extends BaseRepository<ActivityType, Long> {

    @Cacheable(cacheNames = ACTIVITY_TYPE_LIST_BY_PARENT_ID_CACHE)
    List<ActivityType> findAllByParentIdAndDeletedIsFalse(Long parentId);

    @Cacheable(cacheNames = ACTIVITY_TYPE_LIST_CACHE)
    List<ActivityType> findAllByParentIdIsNullAndDeletedIsFalse();
}
