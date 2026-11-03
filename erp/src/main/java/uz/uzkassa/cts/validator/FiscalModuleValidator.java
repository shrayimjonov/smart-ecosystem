package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.fm.FiscalModuleDTO;
import uz.uzkassa.cts.repository.FiscalModuleRepository;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FiscalModuleValidator extends BaseValidator implements ValidatorStrategy<FiscalModuleDTO> {

    FiscalModuleRepository fiscalModuleRepository;

    public FiscalModuleValidator(LocalizationMessage localizationMessage, FiscalModuleRepository fiscalModuleRepository) {
        super(localizationMessage);
        this.fiscalModuleRepository = fiscalModuleRepository;
    }

    @Override
    public void validate(FiscalModuleDTO dto) {
        validateRequiredField(dto.getSerialNumber(), SERIAL_NUMBER);
        validateExistsInDatabase(fiscalModuleRepository.existsBySerialNumber(dto.getSerialNumber().toUpperCase()), FISCAL_MODULE, SERIAL_NUMBER, dto.getSerialNumber().toUpperCase());
    }
}
