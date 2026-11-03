package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.enums.CommonStatus;

import java.util.List;

@Repository
public interface PositionRepository extends BaseRepository<Position, Long> {

    @Cacheable(cacheNames = CacheConstants.POSITION_ENTITY_LIST_CACHE)
    List<Position> findAllByDeletedIsFalse();

    @Cacheable(cacheNames = CacheConstants.POSITION_ENTITY_LIST_BY_STATUS_CACHE)
    List<Position> findAllByCompanyIdAndStatusAndDeletedIsFalse(Long companyId, CommonStatus status);

    List<Position> findAllByCompanyIdAndParentIdIsNullAndStatus(Long companyId, CommonStatus status);

}
