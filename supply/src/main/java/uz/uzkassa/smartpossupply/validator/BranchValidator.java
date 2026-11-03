package uz.uzkassa.smartpossupply.validator;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.dto.branch.BranchDTO;
import uz.uzkassa.smartpossupply.repository.BranchRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

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
        Long companyId = Optional.ofNullable(dto.getCompanyId()).orElseGet(SecurityUtils::getCurrentCompanyId);
        if (companyId == null) {
            throw new BadRequestException(localizationMessage.localize(MessageConstants.COMPANY_NOT_REGISTERED));
        }

        if (dto.getId() == null) {
            validateExistsInDatabase(branchRepository.existsByCompanyIdAndNameIgnoreCaseAndDeletedIsFalse(companyId, dto.getName()), BRANCH, NAME, dto.getName());
        } else {
            validateExistsInDatabase(branchRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNotAndDeletedIsFalse(companyId, dto.getName(), dto.getId()), BRANCH, NAME, dto.getName());

        }
        if (StringUtils.hasText(dto.getBranchNumber())) {
            if (dto.getId() == null) {
                validateExistsInDatabase(branchRepository.existsByCompanyIdAndBranchNumberAndDeletedIsFalse(companyId, dto.getBranchNumber()), BRANCH, NUMBER, dto.getBranchNumber());
            }
            if (dto.getId() != null) {
                validateExistsInDatabase(branchRepository.existsByCompanyIdAndBranchNumberAndIdNotAndDeletedIsFalse(companyId, dto.getBranchNumber(), dto.getId()), BRANCH, NUMBER, dto.getBranchNumber());
            }
        }
    }
}
