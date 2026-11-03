package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.OrderRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderNumbering extends AbstractNumbering {

    OrderRepository orderRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.ORDER;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return orderRepository.getMaxIncrementNumber(companyId);
    }
}
