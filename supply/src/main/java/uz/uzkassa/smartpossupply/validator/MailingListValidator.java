package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListDTO;
import uz.uzkassa.smartpossupply.repository.MailingListRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MailingListValidator extends BaseValidator implements ValidatorStrategy<MailingListDTO> {

    MailingListRepository mailingListRepository;

    public MailingListValidator(LocalizationMessage localizationMessage, MailingListRepository mailingListRepository) {
        super(localizationMessage);
        this.mailingListRepository = mailingListRepository;
    }

    @Override
    public void validate(MailingListDTO dto) {
        if (!SecurityUtils.isFromAdmin()) {
            if (dto.getId() == null) {
                validateExistsInDatabase(mailingListRepository.existsByNameAndCompanyId(dto.getName(), SecurityUtils.getCompanyId()), MAILING_LIST, NAME, dto.getName());
            } else {
                validateExistsInDatabase(mailingListRepository.existsByNameAndCompanyIdAndIdNot(dto.getName(), SecurityUtils.getCompanyId(), dto.getId()), MAILING_LIST, NAME, dto.getName());
            }
        }
    }

}
