package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.dto.address.RegionDistrictDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillRegionDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.repository.RegionRepository;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class RegionService extends BaseService {

    RegionRepository regionRepository;
    FacturaRestClient facturaRestClient;
    CacheService cacheService;

    public List<RegionDistrictDTO> getItems() {
        return regionRepository
            .findAll()
            .stream()
            .map(Region::toDTO)
            .toList();
    }

    @Transactional
    public void syncRegions() {
        WaybillRegionDTO[] regions = facturaRestClient.getRegions();
        if (regions != null) {
            List<Region> regionList = new ArrayList<>();
            for (WaybillRegionDTO dto : regions) {
                if (dto.getRegionId() != 50) {
                    Region region = regionRepository.findByCode(dto.getRegionId()).orElse(null);
                    if (region == null) {
                        region = new Region();
                    }
                    region.setCode(dto.getRegionId());
                    region.setNameUz(dto.getNameUzLatn());
                    region.setNameCyrillic(dto.getNameUzCyrl());
                    region.setNameRu(dto.getNameRu());
                    regionList.add(region);
                }
            }
            if (!CollectionUtils.isEmpty(regionList)) {
                regionRepository.saveAll(regionList);
                regionRepository.flush();
            }
            cacheService.clearByEntityType(EntityTypeEnum.REGION);
        }
    }
}
