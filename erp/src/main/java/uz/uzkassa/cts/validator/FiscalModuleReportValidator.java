package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportRequestDTO;
import uz.uzkassa.cts.repository.FiscalModuleReportRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FiscalModuleReportValidator extends BaseValidator implements ValidatorStrategy<FiscalModuleReportRequestDTO> {

    FiscalModuleReportRepository fiscalModuleReportRepository;

    public FiscalModuleReportValidator(LocalizationMessage localizationMessage, FiscalModuleReportRepository fiscalModuleReportRepository) {
        super(localizationMessage);
        this.fiscalModuleReportRepository = fiscalModuleReportRepository;
    }

    @Override
    public void validate(FiscalModuleReportRequestDTO dto) {
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
        validateRequiredField(dto.getDeviceSerialNumber(), SERIAL_NUMBER);
        validateRequiredField(dto.getFmSerialNumber(), SERIAL_NUMBER);
        validateRequiredField(dto.getStatus(), STATUS);
        validateExistsInDatabase(fiscalModuleReportRepository.existsByFmSerialNumber(dto.getFmSerialNumber()), FISCAL_MODULE, SERIAL_NUMBER, dto.getFmSerialNumber());
    }
}
