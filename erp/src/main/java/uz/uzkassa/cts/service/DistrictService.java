package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.util.List;

public interface DistrictService extends BaseService<District, Long> {

    List<CommonDTO> getItemsByRegionId(Long regionId);
}
