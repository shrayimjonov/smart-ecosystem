package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.repository.StockRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockReturnNumbering extends AbstractNumbering {

    StockRepository stockRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.STOCK_RETURN;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return stockRepository.getMaxIncrementNumber(companyId, StockTransferType.RETURN);
    }
}
