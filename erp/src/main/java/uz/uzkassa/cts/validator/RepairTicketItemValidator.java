package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.repairticket.RepairTicketItemDTO;

@Component
public class RepairTicketItemValidator extends BaseValidator implements ValidatorStrategy<RepairTicketItemDTO> {

    public RepairTicketItemValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(RepairTicketItemDTO dto) {
        validateRequiredField(dto.getProductId(), PRODUCT);
        validateRequiredField(dto.getQty(), QTY);
    }
}
