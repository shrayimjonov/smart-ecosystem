package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.domain.address.District;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.repository.DistrictRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DistrictService extends BaseService {

    DistrictRepository districtRepository;

    public List<CommonDTO> getItemsByRegionId(Long regionId) {
        return districtRepository.findAllByRegionId(regionId).stream().map(District::toCommonDTO).collect(Collectors.toList());
    }
}
