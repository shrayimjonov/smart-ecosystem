package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SupplierValidator extends BaseValidator implements ValidatorStrategy<SupplierDTO> {

    public SupplierValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(SupplierDTO dto) {
        validateRequiredField(dto.getName(), NAME);
    }
}
