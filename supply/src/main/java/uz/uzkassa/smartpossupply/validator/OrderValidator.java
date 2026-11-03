package uz.uzkassa.smartpossupply.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.order.OrderDTO;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OrderValidator extends BaseValidator implements ValidatorStrategy<OrderDTO> {

    public OrderValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(OrderDTO dto) {
        validateRequiredField(dto.getBranchId(), BRANCH);
        validateRequiredField(dto.getProducts(), PRODUCTS);
    }
}
