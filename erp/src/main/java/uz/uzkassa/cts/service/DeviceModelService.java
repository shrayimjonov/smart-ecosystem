package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.DeviceModel;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;

import java.util.List;

public interface DeviceModelService extends BaseService<DeviceModel, Long> {

    CommonCodeDTO get(Long id);

    List<CommonCodeDTO> getItems();
}
