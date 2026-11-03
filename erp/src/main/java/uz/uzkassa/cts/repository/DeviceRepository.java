package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.repository.custom.DeviceRepositoryCustom;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DeviceRepository extends BaseRepository<Device, Long>, DeviceRepositoryCustom {

    Device findByModelIdAndSerialNumber(Long modelId, String serialNumber);

    boolean existsBySerialNumberAndModelId(String serialNumber, Long modelId);

    @Cacheable(cacheNames = CacheConstants.DEVICE_BY_SERIAL_NUMBER)
    Device findBySerialNumber(String serialNumber);

    Device findFirstBySerialNumber(String serialNumber);

    Set<Device> findBySerialNumberIn(List<String> serialNumbers);

    Set<Device> findByIdIn(List<Long> ids);

    Optional<Device> findByIdOrSerialNumber(Long id, String serialNumber);
}
