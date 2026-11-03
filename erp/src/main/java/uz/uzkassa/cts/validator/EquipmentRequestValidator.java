package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.equipmentrequest.EquipmentRequestDTO;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentRequestValidator extends BaseValidator implements ValidatorStrategy<EquipmentRequestDTO> {

    public EquipmentRequestValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(EquipmentRequestDTO dto) {
        validateRequiredField(dto.getRequestDate(), DATE);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getApproverId(), APPROVER);
        validateRequiredField(dto.getItems(), ITEMS);
        dto.getItems().forEach(equipmentRequestItemDTO -> {
            validateRequiredField(equipmentRequestItemDTO.getProductId(), PRODUCT);
            validateRequiredField(equipmentRequestItemDTO.getQuantity(), QTY);
        });
    }
}
