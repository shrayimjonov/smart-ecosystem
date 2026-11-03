package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.domain.address.Region;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.repository.RegionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RegionService extends BaseService {

    RegionRepository regionRepository;

    public List<CommonDTO> getItems() {
        return regionRepository.findAll().stream().map(Region::toCommonDTO).collect(Collectors.toList());
    }
}
