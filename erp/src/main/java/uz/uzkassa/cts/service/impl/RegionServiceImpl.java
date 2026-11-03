package uz.uzkassa.cts.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.repository.RegionRepository;
import uz.uzkassa.cts.service.RegionService;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class RegionServiceImpl extends BaseServiceImpl<Region, Long> implements RegionService {

    RegionRepository regionRepository;

    public RegionServiceImpl(RegionRepository regionRepository) {
        super(regionRepository);
        this.regionRepository = regionRepository;
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.REGION_ITEMS_CACHE)
    public List<CommonDTO> getItems() {
        return regionRepository.findAllByDeletedIsFalse().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }
}
