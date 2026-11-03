package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.AggregationOrderRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AggregationOrderNumbering extends AbstractNumbering {

    AggregationOrderRepository aggregationOrderRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.AGGREGATION_ORDER;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return aggregationOrderRepository.getMaxIncrementNumber(companyId);
    }
}
