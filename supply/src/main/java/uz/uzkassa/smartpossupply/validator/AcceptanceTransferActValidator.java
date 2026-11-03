package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActDTO;
import uz.uzkassa.smartpossupply.repository.CompanyRepository;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AcceptanceTransferActValidator extends BaseValidator implements ValidatorStrategy<AcceptanceTransferActDTO> {

    CompanyRepository companyRepository;

    public AcceptanceTransferActValidator(LocalizationMessage localizationMessage, CompanyRepository companyRepository) {
        super(localizationMessage);
        this.companyRepository = companyRepository;
    }

    @Override
    public void validate(AcceptanceTransferActDTO dto) {
        if (!StringUtils.hasText(dto.getSellerPinfl())) {
            throw new BadRequestException("Seller Pinfl is required");
        }
        if (!StringUtils.hasText(dto.getBuyerTinOrPinfl())) {
            throw new BadRequestException("Buyer Tin Or Pinfl is required");
        }
        if (companyRepository.findByTinAndDeletedIsFalse(dto.getSellerPinfl()).isEmpty()) {
            throw new NotFoundException("Seller not found by this TIN:".concat(dto.getSellerPinfl()));
        }
        if (companyRepository.findByTinAndDeletedIsFalse(dto.getBuyerTinOrPinfl()).isEmpty()) {
            throw new NotFoundException("Buyer not found by this TIN:".concat(dto.getBuyerTinOrPinfl()));
        }
    }
}
