package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.personalidentityinformation.PersonalIdentityInformationDTO;
import uz.uzkassa.cts.enums.PersonalIdentityType;
import uz.uzkassa.cts.repository.PersonalIdentityInformationRepository;

@Component
public class PersonalIdentityInformationValidator extends BaseValidator implements ValidatorStrategy<PersonalIdentityInformationDTO> {

    private final PersonalIdentityInformationRepository personalIdentityInformationRepository;

    public PersonalIdentityInformationValidator(LocalizationMessage localizationMessage, PersonalIdentityInformationRepository personalIdentityInformationRepository) {
        super(localizationMessage);
        this.personalIdentityInformationRepository = personalIdentityInformationRepository;
    }

    @Override
    public void validate(PersonalIdentityInformationDTO dto) {
        validateRequiredField(dto.getSerial(), SERIAL_NUMBER);
        validateRequiredField(dto.getNumber(), "Number");
        validateRequiredField(dto.getIssueDate(), DATE);
        validateRequiredField(dto.getExpiryDate(), DATE);
        validateRequiredField(dto.getType(), TYPE);
        if (dto.getType() == PersonalIdentityType.PASSPORT) {
            validateRequiredField(dto.getIssueBy(), ISSUED_PERSON);
            validateRequiredField(dto.getPinfl(), PINFL);
        }
        if (dto.getType() == PersonalIdentityType.DRIVER_LICENSE) {
            validateRequiredField(dto.getCategories(), CATEGORY);
        }
        if (dto.getId() == null) {
            validateExistsInDatabase(personalIdentityInformationRepository.existsByEmployeeIdAndTypeAndDeletedIsFalse(dto.getEmployeeId(), dto.getType()), PERSONAL_IDENTITY_INFORMATION, dto.getType().name(), dto.getType().getName());
        } else {
            validateExistsInDatabase(personalIdentityInformationRepository.existsByEmployeeIdAndTypeAndIdNotAndDeletedIsFalse(dto.getEmployeeId(), dto.getType(), dto.getId()), PERSONAL_IDENTITY_INFORMATION, dto.getType().name(), dto.getType().getName());
        }
    }
}
