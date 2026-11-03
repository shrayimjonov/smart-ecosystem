package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.whitelist.WhiteListNewDTO;
import uz.uzkassa.cts.repository.WhiteListRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class WhiteListValidator extends BaseValidator implements ValidatorStrategy<WhiteListNewDTO> {

    WhiteListRepository whiteListRepository;

    public WhiteListValidator(LocalizationMessage localizationMessage, WhiteListRepository whiteListRepository) {
        super(localizationMessage);
        this.whiteListRepository = whiteListRepository;
    }

    @Override
    public void validate(WhiteListNewDTO dto) {
        validateRequiredField(dto.getCustomerId(), CUSTOMER);
        validateRequiredField(dto.getAction(), ACTION);
        if ((dto.getFromDate() != null)) {
            validateRequiredField(dto.getToDate(), DATE);
        }
        if ((dto.getToDate() != null)) {
            validateRequiredField(dto.getFromDate(), DATE);
        }
        validateRequiredField(dto.getDescription(), DESCRIPTION);

        if (dto.getId() == null) {
            validateExistsInDatabase(whiteListRepository.existsByCustomerIdAndAction(dto.getCustomerId(), dto.getAction()), CUSTOMER, ACTION, String.valueOf(dto.getAction()));
        } else {
            validateExistsInDatabase(whiteListRepository.existsByCustomerIdAndActionAndIdNot(dto.getCustomerId(), dto.getAction(), dto.getId()), CUSTOMER, ACTION, String.valueOf(dto.getAction()));
        }
    }
}
