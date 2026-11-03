package uz.uzkassa.cts.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.DeviceModel;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.DeviceModelType;
import uz.uzkassa.cts.repository.DeviceModelRepository;
import uz.uzkassa.cts.service.DeviceModelService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class DeviceModelServiceImpl extends BaseServiceImpl<DeviceModel, Long> implements DeviceModelService {

    private final DeviceModelRepository deviceModelRepository;

    public DeviceModelServiceImpl(DeviceModelRepository deviceModelRepository) {
        super(deviceModelRepository);
        this.deviceModelRepository = deviceModelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CommonCodeDTO get(Long id) {
        DeviceModel deviceModel = deviceModelRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE_MODEL));
        return deviceModel.toCommonCodeDTO();
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConstants.MODEL_LIST_CACHE)
    public List<CommonCodeDTO> getItems() {
        List<DeviceModel> deviceModels;
        if (!isSuperAdmin() && getCompanyId() != null) {
            deviceModels = deviceModelRepository.findAllByCompanyId(getCompanyId()).stream().filter(deviceModel -> !DeviceModelType.SW.name().equals(deviceModel.getCode())).collect(Collectors.toList());
        } else {
            deviceModels = deviceModelRepository.findAll().stream().filter(deviceModel -> !DeviceModelType.SW.name().equals(deviceModel.getCode())).collect(Collectors.toList());
        }
        return deviceModels.stream().map(DeviceModel::toCommonCodeDTO).collect(Collectors.toList());
    }
}
