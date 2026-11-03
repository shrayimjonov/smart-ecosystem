package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.container.ContainerDTO;
import uz.uzkassa.smartpossupply.repository.ContainerRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ContainerValidator extends BaseValidator implements ValidatorStrategy<ContainerDTO> {

    ContainerRepository containerRepository;

    public ContainerValidator(LocalizationMessage localizationMessage, ContainerRepository containerRepository) {
        super(localizationMessage);
        this.containerRepository = containerRepository;
    }

    @Override
    public void validate(ContainerDTO dto) {
        validateRequiredField(dto.getItems(), ITEMS);
        if (dto.getId() == null) {
            validateExistsInDatabase(containerRepository.existsByCompanyIdAndIdentificationNumberIgnoreCase(SecurityUtils.getCompanyId(), dto.getIdentificationNumber()), CONTAINER, ID, dto.getIdentificationNumber());
        } else {
            validateExistsInDatabase(containerRepository.existsByCompanyIdAndIdentificationNumberIgnoreCaseAndIdNot(SecurityUtils.getCompanyId(), dto.getIdentificationNumber(), dto.getId()), CONTAINER, ID, dto.getIdentificationNumber());
        }
    }
}
