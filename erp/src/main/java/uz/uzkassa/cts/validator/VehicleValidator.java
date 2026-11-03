package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.vehicle.VehicleDTO;

@Component
public class VehicleValidator extends BaseValidator implements ValidatorStrategy<VehicleDTO> {

    public VehicleValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(VehicleDTO dto) {
        validateRequiredField(dto.getEmployeeId(), EMPLOYEE);
        validateRequiredField(dto.getModel(), MODEL);
        validateRequiredField(dto.getColor(), COLOR);
        validateRequiredField(dto.getNumberPlate(), NUMBER_PLATE);
    }
}
