package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.banktariff.BankTariffDTO;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankTariffValidator extends BaseValidator implements ValidatorStrategy<BankTariffDTO> {


    public BankTariffValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(BankTariffDTO dto) {
        validateRequiredField(dto.getHumoPercentage(), "humoPercentage");
        validateRequiredField(dto.getVisaPercentage(), "visaPercentage");
        validateRequiredField(dto.getVisaDomesticPercentage(), "visaDomesticPercentage");
        validateRequiredField(dto.getUzcardPercentage(), "uzcardPercentage");
        validateRequiredField(dto.getXizmatId(), XIZMAT);
        validateRequiredField(dto.getAppType(), APP_TYPE);
        validateRequiredField(dto.getAppType(), BANK);
    }
}
