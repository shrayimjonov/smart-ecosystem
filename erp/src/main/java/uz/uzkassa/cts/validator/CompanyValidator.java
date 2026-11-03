package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.company.base.CompanyDTO;
import uz.uzkassa.cts.repository.CompanyRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompanyValidator extends BaseValidator implements ValidatorStrategy<CompanyDTO> {

    CompanyRepository companyRepository;

    public CompanyValidator(LocalizationMessage localizationMessage, CompanyRepository companyRepository) {
        super(localizationMessage);
        this.companyRepository = companyRepository;
    }

    @Override
    public void validate(CompanyDTO dto) {
        validateRequiredField(dto.getTin(), TIN);
        validateRequiredField(dto.getName(), NAME);

        if (dto.getId() == null) {
            validateExistsInDatabase(companyRepository.existsByTin(dto.getTin()), COMPANY, TIN, dto.getTin());
            validateExistsInDatabase(companyRepository.existsByNameIsIgnoreCase(dto.getName()), COMPANY, NAME, dto.getName());
        } else {
            validateExistsInDatabase(companyRepository.existsByTinAndIdNot(dto.getTin(), dto.getId()), COMPANY, TIN, dto.getTin());
            validateExistsInDatabase(companyRepository.existsByNameIsIgnoreCaseAndIdNot(dto.getName(), dto.getId()), COMPANY, NAME, dto.getName());
        }
    }
}
