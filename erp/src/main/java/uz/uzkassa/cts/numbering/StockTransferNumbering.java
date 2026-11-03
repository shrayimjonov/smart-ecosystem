package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.StockTransferRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockTransferNumbering extends AbstractNumbering {

    StockTransferRepository stockTransferRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.STOCK_TRANSFER;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return stockTransferRepository.getMaxIncrementNumber(companyId);
    }
}
