package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.Advertisement;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementAdminDto;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementCreateDto;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementDeviceListDto;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.filter.AdvertisementFilter;
import uz.uzkassa.smartpos.mappers.AdvertisementMapper;
import uz.uzkassa.smartpos.repository.postgres.AdvertisementRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementService extends BaseService {

    private final AdvertisementMapper advertisementMapper;
    private final DeviceRepository deviceRepository;
    private final AdvertisementRepository advertisementRepository;

    public Long create(AdvertisementCreateDto dto) {
        Advertisement advertisement = advertisementMapper.toEntity(dto);
        advertisement.setCreatedById(SecurityUtils.getCurrentUserId());
        advertisement.setLastModifiedById(SecurityUtils.getCurrentUserId());
        advertisementRepository.save(advertisement);
        return advertisement.getId();
    }

    public void update(Long id, AdvertisementCreateDto dto) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow();
        advertisementMapper.partialUpdate(dto, advertisement);
        advertisement.setCreatedById(SecurityUtils.getCurrentUserId());
        advertisement.setLastModifiedById(SecurityUtils.getCurrentUserId());
        advertisementRepository.save(advertisement);
    }

    public AdvertisementAdminDto findById(Long id) {
        return advertisementRepository.findById(id)
                .map(advertisementMapper::toAdminDto)
                .orElseThrow();
    }

    public void delete(Long id) {
        advertisementRepository.deleteById(id);
    }

    public List<AdvertisementDeviceListDto> getAdsForDevice(String deviceSN) {
        Device device = deviceRepository.findFirstBySerialNumber(deviceSN).orElseThrow(notFound(localizationService.getMessage("device")));
        return advertisementRepository.advertisementForDevice(device);
    }

    public PageDto<AdvertisementAdminDto> findAllByFilter(AdvertisementFilter filter) {
        return advertisementRepository.findAllByFilter(filter);
    }

}
