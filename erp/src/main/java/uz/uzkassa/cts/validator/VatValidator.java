package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.vat.admin.VatAdminDTO;
import uz.uzkassa.cts.repository.VatRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class VatValidator extends BaseValidator implements ValidatorStrategy<VatAdminDTO> {

    VatRepository vatRepository;

    public VatValidator(LocalizationMessage localizationMessage, VatRepository vatRepository) {
        super(localizationMessage);
        this.vatRepository = vatRepository;
    }

    @Override
    public void validate(VatAdminDTO dto) {
        validateRequiredField(dto.getNameUzLatin(), NAME_UZ_LATIN);
        validateRequiredField(dto.getNameUzCyrillic(), NAME_UZ_CYRILLIC);
        validateRequiredField(dto.getNameRu(), NAME_RU);
        if (dto.getId() == null) {
            validateExistsInDatabase(vatRepository.existsByPercent(dto.getPercent()), VAT, PERCENT, dto.getPercent().toString());
        } else {
            validateExistsInDatabase(vatRepository.existsByPercentAndIdNot(dto.getPercent(), dto.getId()), VAT, PERCENT, dto.getPercent().toString());
        }
    }
}
