package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.branch.BranchDTO;
import uz.uzkassa.cts.repository.BranchRepository;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.cts.security.SecurityUtils.getCompanyId;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BranchValidator extends BaseValidator implements ValidatorStrategy<BranchDTO> {

    BranchRepository branchRepository;

    public BranchValidator(LocalizationMessage localizationMessage, BranchRepository branchRepository) {
        super(localizationMessage);
        this.branchRepository = branchRepository;
    }

    @Override
    public void validate(BranchDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        Long companyId = Optional.ofNullable(dto.getCompanyId()).orElse(getCompanyId());
        validateRequiredField(companyId, COMPANY);
        if (dto.getId() == null) {
            validateExistsInDatabase(branchRepository.existsByNameIsIgnoreCaseAndCompanyIdAndDeletedIsFalse(dto.getName(), companyId), BRANCH, NAME, dto.getName());
        } else {
            validateExistsInDatabase(branchRepository.existsByNameIsIgnoreCaseAndCompanyIdAndDeletedIsFalseAndIdNot(dto.getName(), companyId, dto.getId()), BRANCH, NAME, dto.getName());
        }
    }
}
