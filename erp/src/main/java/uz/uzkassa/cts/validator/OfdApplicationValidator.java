package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationCreateDTO;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OfdApplicationValidator extends BaseValidator implements ValidatorStrategy<OfdApplicationCreateDTO> {

    FiscalizationRepository fiscalizationRepository;

    public OfdApplicationValidator(LocalizationMessage localizationMessage, FiscalizationRepository fiscalizationRepository) {
        super(localizationMessage);
        this.fiscalizationRepository = fiscalizationRepository;
    }

    @Override
    public void validate(OfdApplicationCreateDTO dto) {
        validateRequiredField(dto.getDeviceId(), DEVICE);
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
        validateRequiredField(dto.getFiscalModuleId(), FISCAL_MODULE);
        if (!fiscalizationRepository.existsByCustomerIdAndDeviceIdAndFiscalModuleId(dto.getCustomerId(), dto.getDeviceId(), dto.getFiscalModuleId())) {
            throw new BadRequestException(localizationMessage.localize(ENTITY_NOT_FOUND, new Object[]{localizationMessage.localize(CUSTOMER)}));
        }
    }
}
