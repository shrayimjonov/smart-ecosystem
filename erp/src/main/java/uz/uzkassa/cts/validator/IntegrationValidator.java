package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.integration.admin.IntegrationDTO;
import uz.uzkassa.cts.repository.IntegrationRepository;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrationValidator extends BaseValidator implements ValidatorStrategy<IntegrationDTO> {

    IntegrationRepository integrationRepository;

    public IntegrationValidator(LocalizationMessage localizationMessage, IntegrationRepository integrationRepository) {
        super(localizationMessage);
        this.integrationRepository = integrationRepository;
    }

    @Override
    public void validate(IntegrationDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getDescription(), DESCRIPTION);
        validateRequiredField(dto.getType(), TYPE);

        if (dto.getId() == null) {
            validateExistsInDatabase(integrationRepository.existsByTypeAndDeletedIsFalse(dto.getType()), INTEGRATION, TYPE, dto.getType().toString());
        } else {
            validateExistsInDatabase(integrationRepository.existsByTypeAndDeletedIsFalseAndIdNot(dto.getType(), dto.getId()), INTEGRATION, TYPE, dto.getType().toString());
        }
    }
}
