package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.bank.BankDTO;
import uz.uzkassa.cts.repository.BankRepository;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankValidator extends BaseValidator implements ValidatorStrategy<BankDTO> {

    BankRepository bankRepository;

    public BankValidator(LocalizationMessage localizationMessage, BankRepository bankRepository) {
        super(localizationMessage);
        this.bankRepository = bankRepository;
    }

    @Override
    public void validate(BankDTO dto) {
        validateRequiredField(dto.getCode(), CODE);
        if (dto.getCode().length() != 5) {
            throw new BadRequestException(localizationMessage.localize(CODE_LENGTH_VALIDATION, new Object[]{localizationMessage.localize(BANK), "5"}));
        }
        if (dto.getId() == null) {
            validateExistsInDatabase(bankRepository.existsByCode(dto.getCode()), BANK, CODE, dto.getCode());
        } else {
            validateExistsInDatabase(bankRepository.existsByCodeAndIdNot(dto.getCode(), dto.getId()), BANK, CODE, dto.getCode());
        }

    }
}
