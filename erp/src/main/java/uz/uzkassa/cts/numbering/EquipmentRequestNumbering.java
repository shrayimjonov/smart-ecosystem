package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.EquipmentRequestRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EquipmentRequestNumbering extends AbstractNumbering {

    EquipmentRequestRepository equipmentRequestRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.EQUIPMENT_REQUEST;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return equipmentRequestRepository.getMaxIncrementNumber(companyId);
    }
}
