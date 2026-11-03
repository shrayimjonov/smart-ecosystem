package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.DeviceModel;

import java.util.List;

@Repository
public interface DeviceModelRepository extends BaseRepository<DeviceModel, Long> {

    @Cacheable(cacheNames = CacheConstants.MODEL_BY_CODE_CACHE)
    DeviceModel findFirstByCode(String code);

    @Cacheable(cacheNames = CacheConstants.MODEL_LIST_CACHE)
    List<DeviceModel> findAll();

    @Cacheable(cacheNames = CacheConstants.MODEL_LIST_CACHE)
    List<DeviceModel> findAllByCompanyId(Long companyId);
}
