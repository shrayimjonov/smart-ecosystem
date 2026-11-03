package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.address.AddressDTO;
import uz.uzkassa.cts.repository.AddressRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AddressValidator extends BaseValidator implements ValidatorStrategy<AddressDTO> {

    AddressRepository addressRepository;

    public AddressValidator(LocalizationMessage localizationMessage, AddressRepository addressRepository) {
        super(localizationMessage);
        this.addressRepository = addressRepository;
    }

    @Override
    public void validate(AddressDTO dto) {
        validateRequiredField(dto.getType(), TYPE);
        validateRequiredField(dto.getRegionId(), REGION);
        validateRequiredField(dto.getDistrictId(), DISTRICT);

        if (dto.getId() == null) {
            validateExistsInDatabase(addressRepository.existsByEntityIdAndEntityTypeAndTypeAndDeletedFalse(dto.getEntityId(), dto.getEntityType(), dto.getType()), ADDRESS, ENTITY, dto.getEntityType().name());
        } else {
            validateExistsInDatabase(addressRepository.existsByEntityIdAndEntityTypeAndTypeAndDeletedFalseAndIdNot(dto.getEntityId(), dto.getEntityType(), dto.getType(), dto.getId()), ADDRESS, ENTITY, dto.getEntityType().name());
        }
    }
}
