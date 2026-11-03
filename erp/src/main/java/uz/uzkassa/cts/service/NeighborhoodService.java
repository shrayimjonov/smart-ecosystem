package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.payload.OfdNeighborhoodSyncPayload;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface NeighborhoodService extends BaseService<Neighborhood, Long> {

    void sync();

    void syncByDistrict(Long districtId);

    void syncFromOfd(OfdNeighborhoodSyncPayload ofdNeighborhoodSyncPayload);

    List<CommonCodeDTO> lookUp(BaseFilter filter);
}
