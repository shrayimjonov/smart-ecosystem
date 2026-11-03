package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.dto.address.RegionDistrictDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDistrictDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class DistrictService extends BaseService {

    FacturaRestClient facturaRestClient;
    CacheService cacheService;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;

    public List<RegionDistrictDTO> getItemsByRegionId(Long regionId) {
        return districtRepository.findAllByRegion_Id(regionId)
            .stream()
            .map(District::toDTO)
            .toList();
    }

    @Transactional
    public void syncDistricts() {
        for (Region region : regionRepository.findAll()) {
            WaybillDistrictDTO[] districts = facturaRestClient.getDistricts(region.getCode());
            List<District> districtList = new ArrayList<>();
            for (WaybillDistrictDTO dto : districts) {
                District district = districtRepository.findByRegionCodeAndCode(region.getCode(), dto.getDistrictCode()).orElse(null);
                if (district == null) {
                    district = new District();
                }
                district.setCode(dto.getDistrictCode());
                district.setRegionCode(dto.getRegionId());
                district.setNameUz(dto.getNameUzLatn());
                district.setNameCyrillic(dto.getNameUzCyrl());
                district.setNameRu(dto.getNameRu());
                district.setRegion(region);
                districtList.add(district);
            }
            if (!CollectionUtils.isEmpty(districtList)) {
                districtRepository.saveAll(districtList);
                districtRepository.flush();
            }
        }
        cacheService.clearByEntityType(EntityTypeEnum.DISTRICT);
    }
}
