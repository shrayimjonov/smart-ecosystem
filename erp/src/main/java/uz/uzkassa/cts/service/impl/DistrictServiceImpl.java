package uz.uzkassa.cts.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.repository.DistrictRepository;
import uz.uzkassa.cts.service.DistrictService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class DistrictServiceImpl extends BaseServiceImpl<District, Long> implements DistrictService {

    private final DistrictRepository districtRepository;

    public DistrictServiceImpl(DistrictRepository districtRepository) {
        super(districtRepository);
        this.districtRepository = districtRepository;
    }

    @Cacheable(cacheNames = CacheConstants.DISTRICT_BY_REGION_ID_CACHE)
    public List<CommonDTO> getItemsByRegionId(Long regionId) {
        return districtRepository.findAllByRegionId(regionId).stream().map(District::toCommonDTO).collect(Collectors.toList());
    }
}
