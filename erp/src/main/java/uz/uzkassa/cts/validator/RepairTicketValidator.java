package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.cts.dto.repairticket.RepairTicketDTO;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

@Component
public class RepairTicketValidator extends BaseValidator implements ValidatorStrategy<RepairTicketDTO> {

    public RepairTicketValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(RepairTicketDTO dto) {
        if (dto.getDeviceId() == null && !StringUtils.hasText(dto.getSerialNumber())) {
            throw new BadRequestException(localizationMessage.localize(REQUIRED, new Object[]{localizationMessage.localize(DEVICE)}));
        }
        validateRequiredField(dto.getTypeId(), REPAIR_TYPE);
        validateRequiredField(dto.getEquipments(), EQUIPMENTS);
        validateRequiredField(dto.getDescription(), DESCRIPTION);
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
    }
}
