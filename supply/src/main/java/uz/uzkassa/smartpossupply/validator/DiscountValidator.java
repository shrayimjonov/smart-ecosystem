package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.dto.discount.DiscountDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountItemDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountItemProductDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountRangeDTO;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.math.BigDecimal;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DiscountValidator extends BaseValidator implements ValidatorStrategy<DiscountDTO> {

    public DiscountValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(DiscountDTO dto) {
        for (DiscountItemDTO itemDTO : dto.getItems()) {
            if (!StringUtils.hasText(itemDTO.getDiscountType())) {
                throw new BadRequestException("Discount item type is required");
            }
            if (!StringUtils.hasText(itemDTO.getRangeType())) {
                throw new BadRequestException("Discount range type is required");
            }
            for (DiscountRangeDTO range : itemDTO.getRanges()) {
                for (DiscountItemProductDTO discountProduct : range.getDiscountProducts()) {
                    if (!StringUtils.hasText(discountProduct.getValueType())) {
                        throw new BadRequestException("Discount value type is required");
                    }

                    if (DiscountValueType.FIXED_PRICE.getCode().equals(discountProduct.getValueType())) {
                        if (Optional.ofNullable(discountProduct.getProductPrice()).orElse(BigDecimal.ZERO).compareTo(discountProduct.getAmount()) < 0) {
                            throw new BadRequestException("Discount price is more than product price");
                        }
                    }
                    if (DiscountValueType.PERCENTAGE.getCode().equals(discountProduct.getValueType())) {
                        if (Optional.ofNullable(discountProduct.getAmount()).orElse(BigDecimal.ZERO).compareTo(BigDecimal.valueOf(100)) > 0) {
                            throw new BadRequestException("Discount percent can not be more than 100 %");
                        }
                    }
                }
            }
        }
    }
}
