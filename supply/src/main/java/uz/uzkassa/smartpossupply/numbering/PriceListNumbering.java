package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.PriceListRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PriceListNumbering extends AbstractNumbering {

    PriceListRepository priceListRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.PRICE_LIST;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return priceListRepository.getMaxIncrementNumber(companyId);
    }
}
