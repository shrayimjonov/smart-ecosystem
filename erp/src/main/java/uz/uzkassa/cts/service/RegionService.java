package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.util.List;

public interface RegionService extends BaseService<Region, Long> {

    List<CommonDTO> getItems();
}
