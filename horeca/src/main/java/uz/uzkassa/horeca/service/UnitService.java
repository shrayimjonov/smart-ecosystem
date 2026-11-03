package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.repository.UnitRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UnitService extends BaseService {

    UnitRepository unitRepository;

    public List<CommonCodeDTO> getItems() {
        return unitRepository.findAll().stream().map(Unit::toCommonCodeDTO).collect(Collectors.toList());
    }
}
