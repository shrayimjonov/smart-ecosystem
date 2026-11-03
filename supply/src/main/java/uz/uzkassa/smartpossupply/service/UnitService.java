package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UnitService extends BaseService {

    public List<UnitDTO> getItems() {
        return unitRepository
                .findAllByDeletedIsFalseOrderBySorder()
                .stream()
                .map(unit -> new UnitDTO(unit.getId(), unit.getName(), unit.getCode(), unit.getMeasureId()))
                .toList();
    }
}
