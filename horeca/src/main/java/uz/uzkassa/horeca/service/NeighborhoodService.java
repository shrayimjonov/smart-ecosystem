package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.domain.address.Neighborhood;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.repository.NeighborhoodRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class NeighborhoodService extends BaseService {

    NeighborhoodRepository neighborhoodRepository;

    public List<CommonDTO> getItemsByDistrictId(Long districtId) {
        return neighborhoodRepository.findAllByDistrictId(districtId).stream().map(Neighborhood::toCommonDTO).collect(Collectors.toList());
    }
}
