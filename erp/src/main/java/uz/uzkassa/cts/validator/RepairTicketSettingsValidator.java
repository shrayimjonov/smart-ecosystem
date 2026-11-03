package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsDTO;

@Component
public class RepairTicketSettingsValidator extends BaseValidator implements ValidatorStrategy<RepairTicketSettingsDTO> {

    public RepairTicketSettingsValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(RepairTicketSettingsDTO dto) {
        validateRequiredField(dto.getProductId(), PRODUCT);
        validateRequiredField(dto.getTypeId(), TYPE);
    }
}
