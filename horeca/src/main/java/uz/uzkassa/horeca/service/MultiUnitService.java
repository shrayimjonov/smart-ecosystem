package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.unit.MultiUnit;
import uz.uzkassa.horeca.dto.unit.MultiUnitDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.interfaces.Validatable;
import uz.uzkassa.horeca.repository.MultiUnitRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MultiUnitService extends BaseService implements Validatable<MultiUnitDTO> {

    MultiUnitRepository multiUnitRepository;

    @Override
    public void validate(MultiUnitDTO multiUnitDTO) {
        if (multiUnitDTO.getWeight() == null || multiUnitDTO.getWeight() == 0d) {
            throw badRequestExceptionThrow("Weight is required").get();
        }
        if (multiUnitDTO.getPrice() == null || BigDecimal.ZERO.compareTo(multiUnitDTO.getPrice()) >= 0) {
            throw badRequestExceptionThrow("Price is required").get();
        }
        if (multiUnitDTO.getMenuItemId() == null) {
            throw badRequestExceptionThrow("Menu item id required").get();
        }
    }

    public List<MultiUnitListDTO> getList(Long menuItemId) {
        return multiUnitRepository.findAllByMenuItemIdAndDeletedIsFalse(menuItemId)
                .stream()
                .map(this::toMultiUnitListDTO).collect(Collectors.toList());
    }

    @Transactional
    public void batchCreate(List<MultiUnitDTO> multiUnitDTOList, Long menuItemId) {
        List<MultiUnit> multiUnits = multiUnitDTOList
                .stream()
                .map(multiUnitDTO -> {
                    multiUnitDTO.setMenuItemId(menuItemId);
                    this.validate(multiUnitDTO);
                    MultiUnit multiUnit = new MultiUnit();
                    multiUnit.setWeight(multiUnitDTO.getWeight());
                    multiUnit.setPrice(multiUnitDTO.getPrice());
                    multiUnit.setMenuItemId(menuItemId);
                    multiUnit.setTechCardId(multiUnitDTO.getTechCardId());
                    return multiUnit;
                }).collect(Collectors.toList());
        multiUnitRepository.saveAll(multiUnits);
    }

    @Transactional
    public void batchUpdate(List<MultiUnitDTO> multiUnitDTOList, Long menuItemId) {
        List<MultiUnit> multiUnitList = multiUnitRepository.findAllByMenuItemIdAndDeletedIsFalse(menuItemId);
        Map<Long, MultiUnit> multiUnitsMap = multiUnitList
                .stream()
                .collect(Collectors.toMap(MultiUnit::getId, Function.identity()));

        List<MultiUnit> multiUnits = multiUnitDTOList
                .stream()
                .map(multiUnitDTO -> {
                    multiUnitDTO.setMenuItemId(menuItemId);
                    this.validate(multiUnitDTO);
                    MultiUnit multiUnit;
                    if (multiUnitDTO.getId() != null && multiUnitsMap.get(multiUnitDTO.getId()) != null) {
                        multiUnit = multiUnitsMap.get(multiUnitDTO.getId());
                    } else {
                        multiUnit = new MultiUnit();
                    }
                    multiUnit.setWeight(multiUnitDTO.getWeight());
                    multiUnit.setPrice(multiUnitDTO.getPrice());
                    multiUnit.setMenuItemId(menuItemId);
                    multiUnit.setTechCardId(multiUnitDTO.getTechCardId());
                    if (multiUnitDTO.getId() != null) {
                        multiUnitsMap.remove(multiUnitDTO.getId());
                    }
                    return multiUnit;
                }).collect(Collectors.toList());

        if (!multiUnitsMap.isEmpty()) {
            multiUnitRepository.deleteAllByIdIn(multiUnitsMap.keySet());
        }
        multiUnitRepository.saveAll(multiUnits);
    }

    @Transactional
    public void deleteByMenuItemId(Long menuItemId) {
        multiUnitRepository.deleteAllByMenuItemId(menuItemId);
    }
}
