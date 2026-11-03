package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.StockAdjustmentRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentNumbering extends AbstractNumbering {

    StockAdjustmentRepository stockAdjustmentRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.STOCK_ADJUSTMENT;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return stockAdjustmentRepository.getMaxIncrementNumber(companyId);
    }
}
