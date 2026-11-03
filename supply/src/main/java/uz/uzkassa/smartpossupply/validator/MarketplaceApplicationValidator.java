package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationDTO;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MarketplaceApplicationValidator extends BaseValidator implements ValidatorStrategy<MarketplaceApplicationDTO> {

    public MarketplaceApplicationValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(MarketplaceApplicationDTO dto) {
        if (dto.getNameUz() == null && dto.getNameCyrillic() == null && dto.getNameRu() == null) {
            throw new BadRequestException("Name must be filled in one of the languages: uz, cyrillic, ru");
        }
    }
}
